package pl.hycom.mokka.service.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

/**
 * @author Kamil Adamiec (kamil.adamiec@hycom.pl)
 */
@Configuration
public class OpenApiParserConfiguration {

    @Bean
    public OpenApiParsingService openApiParsingService(ConversionService conversionService) {
        return new OpenApiParsingService(conversionService);
    }
}
