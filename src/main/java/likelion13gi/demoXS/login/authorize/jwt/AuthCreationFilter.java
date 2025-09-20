package likelion13gi.demoXS.login.authorize.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthCreationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/users/reissue".equals(request.getRequestURI());
    }
    //true ==> 필터 건너뛰기

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        if (existing != null && existing.isAuthenticated()
        && !(existing instanceof AnonymousAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        String providerId = null;
        try {
            Claims claims = tokenProvider.parseClaimAllowExpired(token); // 만료된 토큰이라도 Subject 자체는 필요하니까
            providerId = claims.getSubject();
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (providerId == null || !providerId.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        var anonymousAuthorities = java.util.Collections.singletonList(
                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ANONYMOUS")
        );

        var preAuth = new PreAuthenticatedAuthenticationToken(
                providerId, "N/A", anonymousAuthorities
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(preAuth);
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}
