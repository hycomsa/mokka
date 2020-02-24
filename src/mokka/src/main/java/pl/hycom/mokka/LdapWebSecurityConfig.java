package pl.hycom.mokka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * Web Security Configuration for LDAP authentication. Property 'web.security.profile' has to be set
 * to 'ldap' to use this configuration.
 *
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@Configuration
@ConditionalOnProperty(name = "web.security.profile", havingValue = "ldap")
@EnableWebSecurity
public class LdapWebSecurityConfig extends WebSecurityConfig {

    @Value("${ldap.server.dn.pattern}")
    private String ldapDnPattern;

    @Value("${ldap.server.url}")
    private String ldapUrl;

    @Autowired
    private UserDetailsContextMapper databaseUserDetailsContextMapper;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationEventPublisher(new DefaultAuthenticationEventPublisher())
            .ldapAuthentication()
            .userDnPatterns(ldapDnPattern)
            .contextSource()
            .url(ldapUrl)
            .and()
            .userDetailsContextMapper(databaseUserDetailsContextMapper);

    }
}
