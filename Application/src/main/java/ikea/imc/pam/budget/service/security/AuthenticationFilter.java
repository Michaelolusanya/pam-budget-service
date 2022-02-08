package ikea.imc.pam.budget.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ikea.imc.pam.budget.service.api.dto.ResponseMessageDTO;
import ikea.imc.pam.budget.service.configuration.properties.OAuthProperties;
import ikea.imc.pam.budget.service.controller.dto.ResponseEntityFactory;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(1)
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final OAuthProperties oAuthProperties;

    public AuthenticationFilter(OAuthProperties oAuthProperties) {
        this.oAuthProperties = oAuthProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        JWTVerifier jv = new JWTVerifier();
        String auth = req.getHeader("Authorization");

        if (!oAuthProperties.getEnabled()) {
            filterChain.doFilter(req, res);
        } else if (isCurrentPathOpen(req.getRequestURI(), req.getMethod())) {
            filterChain.doFilter(req, res);
        } else if (authHeaderHasCorrectTokenSyntax(auth) && jv.isJwtVerified(auth)) {
            filterChain.doFilter(req, res);
        } else {
            send401Response(res);
        }
    }

    void send401Response(HttpServletResponse response) throws ServletException, IOException {
        response.resetBuffer();
        response.setStatus(401);
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        ResponseEntity<ResponseMessageDTO<Object>> responseEntity =
                ResponseEntityFactory.generateResponse(HttpStatus.UNAUTHORIZED);
        response.getOutputStream().print(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
        response.flushBuffer();
    }

    boolean isCurrentPathOpen(String path, String method) {
        return (method.equals("GET") && pathExistsInAllowedPathList(path));
    }

    boolean pathExistsInAllowedPathList(String currentPath) {
        String[] allowedGetEndpoints = {
            "/swagger-ui/index.html",
            "/v3/api-docs/swagger-config",
            "/v3/api-docs",
            "/swagger-ui/oauth2-redirect.html",
            "/swagger-ui/favicon-16x16.png",
            "/swagger-ui/favicon-32x32.png",
            "/swagger-ui.css",
            "/swagger-ui-bundle.js",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-ui.css",
            "/favicon-32x32.png",
            "/favicon-16x16.png",
            "/index.html?configUrl=/v3/api-docs/swagger-config",
            "/favicon.ico",
            "/swagger-ui/swagger-ui.css.map",
            "/swagger-ui/swagger-ui-bundle.js.map",
            "/swagger-ui/swagger-ui-standalone-preset.js.map",
            "http://localhost:23154/v3/api-docs",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-ui-standalone-preset.js"
        };
        for (String allowedPath : allowedGetEndpoints) {
            if (currentPath.equals(allowedPath)) {
                return true;
            }
        }
        return false;
    }

    boolean authHeaderHasCorrectTokenSyntax(String authHeader) {
        if (authHeader != null && authHeader.contains(" ") && authHeader.length() > 10) {
            String[] auth = authHeader.split(" ");
            return (auth[0].equals("Bearer") && auth[1].startsWith("ey"));
        } else {
            return false;
        }
    }
}
