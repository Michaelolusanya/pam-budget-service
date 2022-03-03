package ikea.imc.pam.budget.service.security;

import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AADResourceServerConfig extends AADResourceServerWebSecurityConfigurerAdapter {

    @Value("${ikea.imc.pam.oauth.enabled}")
    private boolean isAuthenticationEnabled;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        if (isAuthenticationEnabled) {
            http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
        }
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(getAllowedPaths());
    }

    private String[] getAllowedPaths() {
        return new String[] {
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/favicon.ico",
            "/favicon-16x16.png",
            "/favicon-32x32.png",
            "/index.html?configUrl=/v3/api-docs/swagger-config",
            "/swagger-ui.css",
            "/swagger-ui-bundle.js",
            "/swagger-ui-standalone-preset.js",
            "/swagger-ui/favicon-16x16.png",
            "/swagger-ui/favicon-32x32.png",
            "/swagger-ui/index.html",
            "/swagger-ui/oauth2-redirect.html",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/swagger-ui.css.map",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-ui-bundle.js.map",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-ui-standalone-preset.js.map"
        };
    }
}
