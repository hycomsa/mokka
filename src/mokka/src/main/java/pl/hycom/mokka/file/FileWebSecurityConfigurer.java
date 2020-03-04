package pl.hycom.mokka.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import pl.hycom.mokka.security.ServiceWebSecurityConfigurer;

/**
 * Configures file serving related security.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Slf4j
class FileWebSecurityConfigurer implements ServiceWebSecurityConfigurer {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.info("Configuring security of File serving service.");

        http.authorizeRequests().antMatchers("/files/**").permitAll();
    }
}
