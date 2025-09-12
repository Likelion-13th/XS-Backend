package likelion13gi.demoXS.login.authorize.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import likelion13gi.demoXS.domain.Address;
import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.login.authorize.jwt.CustomUserDetails;
import likelion13gi.demoXS.login.dto.JwtDto;
import likelion13gi.demoXS.login.service.JpaUserDetailsManager;
import likelion13gi.demoXS.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
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
                "http://localhost.3000"
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
