package zercher.security.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    public static final String HEADER_TITLE = "Authorization";
    public static final String HEADER_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;
    private final TokenUtilities tokenUtilities;

    public AuthenticationTokenFilter(UserDetailsService userDetailsService, TokenUtilities tokenUtilities) {
        this.userDetailsService = userDetailsService;
        this.tokenUtilities = tokenUtilities;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = parseToken(request);
            if (token != null && tokenUtilities.isTokenValid(token)) {
                String username = tokenUtilities.getUsernameFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String parseToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HEADER_TITLE);
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith(HEADER_PREFIX)) {
            return headerAuth.substring(HEADER_PREFIX.length());
        }
        return null;
    }
}
