package likelion13gi.demoXS.login.converter;

import likelion13gi.demoXS.domain.User;
//import likelion13gi.demoXS.login.authorize.dto.JwtDto;
import likelion13gi.demoXS.login.dto.UserRequestDto;

public class UserConverter {
    public static User saveUser(UserRequestDto userReqDto) {
        return User.builder()
                .phoneNumber(userReqDto.getPhoneNumber())
                .usernickname(userReqDto.getUsernickname())
                .build();
    }
//
//    public static JwtDto jwtDto(String access, String refresh, String signIn) {
//        return JwtDto.builder()
//                .accessToken(access)
//                .refreshToken(refresh)
//                .signIn(signIn)
//                .build();
//    }
}
