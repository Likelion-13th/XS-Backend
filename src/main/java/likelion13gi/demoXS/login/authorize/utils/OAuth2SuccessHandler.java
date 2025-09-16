package likelion13gi.demoXS.login.authorize.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13gi.demoXS.domain.Address;
import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.login.authorize.jwt.CustomUserDetails;
import likelion13gi.demoXS.login.authorize.dto.JwtDto;
import likelion13gi.demoXS.login.authorize.service.JpaUserDetailsManager;
import likelion13gi.demoXS.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JpaUserDetailsManager jpaUserDetailsManager;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
        throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String providerId = (String) oAuth2User.getAttributes().get("providerId");
        String nickname = (String) oAuth2User.getAttributes().get("nickname");

        if (!jpaUserDetailsManager.userExists(providerId)) {
            User newUser = User.builder()
                    .providerId(providerId)
                    .usernickname(nickname)
                    .deletable(true)
                    .build();

            newUser.setAddress(new Address("10540", "고양시 덕양구 항공대학로 76", "국제은익관 1생활관 M001"));

            CustomUserDetails userDetails = new CustomUserDetails(newUser);
            jpaUserDetailsManager.createUser(userDetails);
            log.info("신규 회원 등록이용");


        }
        else {
            log.info("기존 회원이용");
        }

        JwtDto jwt = userService.jwtMakeSave(providerId);

        String frontendRedirectUrl = request.getParameter("redirectUrl");
        List<String> authorizeUrls = List.of(
                "https://xsfrontend.netlify.app/",
                "http://localhost:3000"
        );
        if(frontendRedirectUrl != null || authorizeUrls.contains(frontendRedirectUrl)) {
            frontendRedirectUrl = "https://xsfrontend.netlify.app/login";
        }

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUrl)
                .queryParam("accessToken", jwt.getAccessToken())
                .build().toUriString();

        log.info("RedirectUrl: {}", redirectUrl);
    }
}

/* 1) 왜 필요한가?
 - 신규 회원의 경우 회원의 정보들이 DB에 등록되어야 합니다.
 - 로그인에 성공한 유저가 액세스토큰과 리프레시토큰을 발급받아야 하기에 필요합니다.
 2) 없으면/틀리면?
 - 액세스 토큰을 쓸 수가 없습니다. 즉 JWT를 쓴 이유가 사라집니다.
 - 리디렉션이 제대로 되지 않아 로그인에 성공해도 프런트엔드 화면으로 넘어가지지 않습니다.
*/