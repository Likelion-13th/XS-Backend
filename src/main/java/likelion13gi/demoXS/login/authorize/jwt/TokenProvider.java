package likelion13gi.demoXS.login.authorize.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.exception.GeneralException;
import likelion13gi.demoXS.login.authorize.dto.JwtDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private final Key secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public TokenProvider(
            @Value("${JWT_SECRET}") String secretKey,
            @Value("${JWT_EXPIRATION}") long accessTokenExpiration,
            @Value("${JWT_REFRESH_EXPIRATION}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public JwtDto generateTokens(UserDetails userDetails) {
        log.info("JWT 생성 : 사용자 {}", userDetails.getUsername());
        String userId = userDetails.getUsername();
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String accessToken = CreateToken(userId, authorities, accessTokenExpiration);

        String refreshToken = CreateToken(userId, null, refreshTokenExpiration);

        log.info("JWT <UNK> : <UNK> {}", userId);
        return new JwtDto(accessToken, refreshToken);
    }

    public String CreateToken(String providerId, String authorities, long expirationTime) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(new Date(System.currentTimeMillis() + expirationTime))
                .setExpiration(new Date(expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256);

        if (authorities != null) {
            builder.claim("authorities", authorities);
        }

        return builder.compact().toString();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token).getBody();
        }
        catch(ExpiredJwtException e) {
            log.warn("토큰 만료");
            throw e;
        }
        catch(JwtException e) {
            log.warn("JWT 인증 실패");
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }
    }

    public Collection<? extends GrantedAuthority> getAuthFromClaims(Claims claims) {
        String authoritiesString = claims.get("authorities", String.class);
        if (authoritiesString != null && authoritiesString.isEmpty()) {
            log.warn("JWT 정보가 없습니다 : ");
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Arrays.stream(authoritiesString.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    public Claims parseClaimAllowExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token).getBody();
        }
        catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}

/* 1) 왜 필요한가?
 - 먼저 우리 서비스에서는 JWT 토큰을 이용한 인증 방식을 채택하고 있습니다.
 - 즉 인증이 필요하다면, 일단 액세스 토큰과 리프레시 토큰을 만들어 내야 합니다.
 - 또한, 해당 토큰이 만료되진 않았는지, 서명되진 않았는지를 검증해야 인증의 안정성이 보장됩니다.
 2) 없다면/틀리면?
 - JWT 토큰 인증 방식을 채택한 이유가 없어집니다. (인증할 토큰이 없기 때문이죠.)
 - 토큰 생성 절차를 분산시킬 순 있겠지만, 그러면 유지보수하기 껄끄러워집니다.
 */