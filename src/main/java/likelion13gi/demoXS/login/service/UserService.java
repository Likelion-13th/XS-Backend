package likelion13gi.demoXS.login.service;

import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.exception.GeneralException;
//import likelion13gi.demoXS.global.utils.Redis.RedisUtil;
//import likelion13gi.demoXS.login.authorize.dto.JwtDto;
//import likelion13gi.demoXS.login.authorize.jwt.JwtTokenUtils;
//import likelion13gi.demoXS.login.authorize.service.JpaUserDetailsManager;
import likelion13gi.demoXS.login.converter.UserConverter;
import likelion13gi.demoXS.login.dto.UserRequestDto;
import likelion13gi.demoXS.login.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
//    private final JpaUserDetailsManager manager;
//    private final JwtTokenUtils jwtTokenUtils;
//    private final RedisUtil redisUtil;
    // private final AmazonS3Manager amazonS3Manager;

    // 로그인

    // username으로 User찾기
    public User findUserByUserName(String userName) {
        return userRepository.findByUsernickname(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_USERNICKNAME));
    }

    public User findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND_BY_PHONENUMBER));
    }

    public Boolean checkMemberByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public User createUser(UserRequestDto userReqDto) {
        // 새로운 사용자 생성
        User newUser = userRepository.save(UserConverter.saveUser(userReqDto));

        return newUser;
    }
}