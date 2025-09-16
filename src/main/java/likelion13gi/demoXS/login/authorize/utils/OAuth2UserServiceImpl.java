package likelion13gi.demoXS.login.authorize.utils;


import likelion13gi.demoXS.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerId = oAuth2User.getAttributes().get("id").toString();
        Map<String, Object> properties =
                (Map<String, Object>) oAuth2User.getAttributes().getOrDefault("properties", Collections.emptyMap());
        String nickname = properties.getOrDefault("nickname", "카카오 사용자").toString();

        Map<String, Object> extendedAttributes = new HashMap<>(oAuth2User.getAttributes());
        extendedAttributes.put("provider_id", providerId);
        extendedAttributes.put("nickname", nickname);

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                extendedAttributes,
                "provider_id"
        );
    }
}

/* 1) 왜 필요한가?
 - 카카오 소셜 로그인에 성공했을 때, 카카오 OpenAPI를 통해 받아온 정보들을 우리 서비스에 맞게 가공해야 합니다.
 - 특히 providerId는 우리가 유저를 식별하는 PK로 쓸 예정이기에 이를 받아와 저장할 필요가 있습니다.
 - 닉네임, providerId 등의 정보를 받아와서 OAuth2User 객체에 저장하고, 이를 유저에 반영해야 하기에 이 코드가 필요합니다.
 2) 없으면/틀리면?
 - OAuth2SuccessHandler, JpaUserDetailsManager 등 우리 서비스에 있는 대부분의 클래스에서 특정 유저를 찾을 때 providerId를
 사용하는데, Spring에서 관리하는 DefaultOAuth2User에 이 정보가 없어집니다.
 - 즉 이게 없으면 우리는 유저를 식별해내는 기능 자체를 못 씁니다. 사실상 대부분의 로직이 의미가 없어지죠.
 */
