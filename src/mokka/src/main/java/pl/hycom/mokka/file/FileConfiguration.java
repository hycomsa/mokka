package pl.hycom.mokka.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.hycom.mokka.security.ServiceWebSecurityConfigurer;

/**
 * Configuration of file serving related beans.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Configuration
public class FileConfiguration {

    @Bean
    public ServiceWebSecurityConfigurer fileWebSecurityConfigurer(){
        return new FileWebSecurityConfigurer();
    }
}
