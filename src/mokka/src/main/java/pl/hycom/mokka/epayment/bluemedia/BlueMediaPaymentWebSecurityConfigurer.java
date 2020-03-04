package pl.hycom.mokka.epayment.bluemedia;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import pl.hycom.mokka.security.ServiceWebSecurityConfigurer;

/**
 * Configures BlueMedia related security.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Slf4j
class BlueMediaPaymentWebSecurityConfigurer implements ServiceWebSecurityConfigurer {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        log.info("Configuring security of BlueMedia ePayment service.");

        http.csrf().ignoringAntMatchers("/bluemedia/**").and().
            authorizeRequests().antMatchers("/bluemedia/**").permitAll();
    }
}
