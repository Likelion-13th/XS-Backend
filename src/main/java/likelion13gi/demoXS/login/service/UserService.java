package likelion13gi.demoXS.login.service;

import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.exception.GeneralException;
import likelion13gi.demoXS.login.authorize.dto.JwtDto;
import likelion13gi.demoXS.login.authorize.jwt.RefreshToken;
import likelion13gi.demoXS.login.authorize.jwt.TokenProvider;
import likelion13gi.demoXS.login.authorize.repository.RefreshTokenRepository;
import likelion13gi.demoXS.login.authorize.service.JpaUserDetailsManager;
import likelion13gi.demoXS.login.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final JpaUserDetailsManager userDetailsManager;

    public boolean checkMemberByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId).isPresent();
    }

    public Optional<User> findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId);
    }

    public User getAuthenticatedUser(String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_AUTHENTICATED));
    }

    @Transactional
    public void saveRefreshToken(String providerId, String refreshToken) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        RefreshToken token = refreshTokenRepository.findByUser(user)
                .map(existingToken -> {
                    existingToken.updateRefreshToken(refreshToken);
                    return existingToken;
                })
                .orElseGet(() -> {
                    return RefreshToken.builder()
                            .user(user)
                            .refreshToken(refreshToken)
                            .ttl(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)
                            .build();
                });

        refreshTokenRepository.save(token);
    }

    @Transactional
    public JwtDto jwtMakeSave(String providerId) {
        UserDetails userDetails = userDetailsManager.loadUserByUsername(providerId);
        JwtDto jwt = tokenProvider.generateTokens(userDetails);
        saveRefreshToken(providerId, jwt.getRefreshToken());
        return jwt;
    }

    @Transactional
    public JwtDto reissue(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if(accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7); // 'Bearer ' 뒤부터 토큰 값 읽기 시작
        }

        Claims claims;
        try{
            claims = tokenProvider.parseClaimAllowExpired(accessToken);
        } catch (GeneralException e) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        String providerId = claims.getSubject();
        if (providerId == null || providerId.isEmpty()) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        User user = findByProviderId(providerId)
                .orElseThrow(() -> {
                    return new GeneralException(ErrorCode.USER_NOT_FOUND);
                });

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorCode.WRONG_REFRESH_TOKEN));

        if (!tokenProvider.validateToken(refreshToken.getRefreshToken())) {
            refreshTokenRepository.deleteByUser(user);
            throw new GeneralException(ErrorCode.TOKEN_EXPIRED);
        }

        UserDetails userDetails = userDetailsManager.loadUserByUsername(providerId);
        JwtDto newJwt = tokenProvider.generateTokens(userDetails);

        refreshToken.updateRefreshToken(newJwt.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return newJwt;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if(accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        Claims claims = tokenProvider.parseClaims(accessToken);
        String providerId = claims.getSubject();
        if (providerId == null || providerId.isEmpty()) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        User user = findByProviderId(providerId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
    }
}