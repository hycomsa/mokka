package pl.hycom.mokka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import pl.hycom.mokka.service.openapi.OpenAPIToStubMappingListWrapperConverter;
import pl.hycom.mokka.service.openapi.OpenApiSchemaParsingStrategy;
import pl.hycom.mokka.stubbing.WireMockStubMappingConverter;

import java.util.HashSet;
import java.util.Set;


@Configuration
public class ConversionConfiguration {

    @Bean(name = "conversionService")
    public ConversionService getConversionService(){
        ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
        conversionServiceFactoryBean.setConverters(getConverters());
        conversionServiceFactoryBean.afterPropertiesSet();

        return conversionServiceFactoryBean.getObject();
    }

    private Set<Converter> getConverters() {
        Set<Converter> converters = new HashSet<>();

        converters.add(new WireMockStubMappingConverter());
        converters.add(new OpenAPIToStubMappingListWrapperConverter(new OpenApiSchemaParsingStrategy()));

        return converters;
    }
}
