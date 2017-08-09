package pl.hycom.mokka;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link VelocityEngine} configuration class. 
 * @author Piotr Kulasek <piotr.kulasek-szwed@hycom.pl>
 */
@Configuration
public class VelocityEngineConfig {

    @Bean
    public VelocityEngine velocityEngine() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/application.properties"));
        return new VelocityEngine(properties);
    }
}
