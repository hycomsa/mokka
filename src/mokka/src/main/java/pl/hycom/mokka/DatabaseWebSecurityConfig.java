package pl.hycom.mokka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Web Security Configuration for database configuration.
 *
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "web.security.profile", havingValue = "local", matchIfMissing = true)
public class DatabaseWebSecurityConfig extends WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
}
