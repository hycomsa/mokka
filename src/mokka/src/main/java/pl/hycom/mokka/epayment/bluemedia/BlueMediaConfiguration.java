package pl.hycom.mokka.epayment.bluemedia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.hycom.mokka.security.ServiceWebSecurityConfigurer;

/**
 * Configuration of BlueMedia related beans.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Configuration
public class BlueMediaConfiguration {

    @Bean
    public ServiceWebSecurityConfigurer blueMediaPaymentWebSecurityConfigurer(){
        return new BlueMediaPaymentWebSecurityConfigurer();
    }
}
