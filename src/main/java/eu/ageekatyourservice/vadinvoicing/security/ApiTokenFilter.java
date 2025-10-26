package eu.ageekatyourservice.vadinvoicing.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiTokenFilter extends OncePerRequestFilter {

    @Value("${api.token}")
    private String apiToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Only apply to API endpoints
        if (requestPath.startsWith("/api/")) {
            String token = request.getHeader("X-API-Token");
            
            if (token == null || token.isEmpty()) {
                token = request.getParameter("token");
            }
            
            if (apiToken.equals(token)) {
                // Authenticate with API role
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        "api-user", 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_API"))
                    );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or missing API token\"}");
                response.setContentType("application/json");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
