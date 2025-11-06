package likelion13gi.demoXS.login.authorize.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13gi.demoXS.domain.Address;
import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.exception.GeneralException;
import likelion13gi.demoXS.login.authorize.jwt.CustomUserDetails;
import likelion13gi.demoXS.login.authorize.dto.JwtDto;
import likelion13gi.demoXS.login.authorize.service.JpaUserDetailsManager;
import likelion13gi.demoXS.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    private static final List<String> ALLOWED_ORIGINS = List.of(
            "https://xsfrontend.netlify.app",
            "http://localhost:3000"
    );
    private static final String DEFAULT_FRONT_ORIGIN = "https://xsfrontend.netlify.app";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            // // 1) providerId 추출(프로젝트 매핑에 맞춰 조정 가능)
            String providerId = extractProviderId(authentication);
            log.info("// [OAuth2Success] providerId={}", providerId);

            // // 2) JWT 발급(Access/Refresh 생성 및 Refresh 저장)
            JwtDto jwt = userService.jwtMakeSave(providerId);
            log.info("// [OAuth2Success] JWT 발급 완료");

            // // 3) 세션에서 프론트 Origin 회수(+사용 후 제거)
            String frontendRedirectOrigin = (String) request.getSession().getAttribute("FRONT_REDIRECT_URI");
            request.getSession().removeAttribute("FRONT_REDIRECT_URI");

            // // 4) 최종 안전장치(화이트리스트 재검증)
            if (frontendRedirectOrigin == null || !ALLOWED_ORIGINS.contains(frontendRedirectOrigin)) {
                frontendRedirectOrigin = DEFAULT_FRONT_ORIGIN;
            }

            // // 5) 최종 리다이렉트 URL 생성(토큰은 URL 인코딩 권장)
            String redirectUrl = UriComponentsBuilder
                    .fromUriString(frontendRedirectOrigin)
                    .queryParam("accessToken", URLEncoder.encode(jwt.getAccessToken(), StandardCharsets.UTF_8))
                    .build(true)
                    .toUriString();

            log.info("// [OAuth2Success] redirect → {}", redirectUrl);
            response.sendRedirect(redirectUrl);

        } catch (GeneralException e) {
            log.error("// [OAuth2Success] 도메인 예외: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("// [OAuth2Success] 예상치 못한 에러: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String extractProviderId(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2) {
            if (oAuth2.getPrincipal() instanceof DefaultOAuth2User user) {
                Map<String, Object> attrs = user.getAttributes();
                Object v = attrs.getOrDefault("providerId", attrs.get("id")); // // Kakao 기본: "id"
                if (v == null) throw new GeneralException(ErrorCode.UNAUTHORIZED);
                return String.valueOf(v);
            }
        }
        throw new GeneralException(ErrorCode.UNAUTHORIZED);
    }
}


/* 1) 왜 필요한가?
 - 신규 회원의 경우 회원의 정보들이 DB에 등록되어야 합니다.
 - 로그인에 성공한 유저가 액세스토큰과 리프레시토큰을 발급받아야 하기에 필요합니다.
 2) 없으면/틀리면?
 - 액세스 토큰을 쓸 수가 없습니다. 즉 JWT를 쓴 이유가 사라집니다.
 - 리디렉션이 제대로 되지 않아 로그인에 성공해도 프런트엔드 화면으로 넘어가지지 않습니다.
*/