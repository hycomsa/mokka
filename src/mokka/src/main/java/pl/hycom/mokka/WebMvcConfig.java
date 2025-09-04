package pl.hycom.mokka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.hycom.mokka.emulator.mock.MockInterceptor;
import pl.hycom.mokka.security.ChangePasswordInterceptor;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(mockInterceptor()).excludePathPatterns("/configurations/**", "/configuration/**", "/users/**", "/user/**", "/files/**", "/logs/**", "/js/**", "/css/**", "/img/**", "/fonts/**", "/api/**", "/login", "/logout", "/change-password/**", "/", "/templates/**", "/actuator/**", "/error", "/error/**");
		registry.addInterceptor(changePasswordInterceptor());
	}

	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setResolveLazily(true);
		return resolver;
	}

	@Bean
	public ChangePasswordInterceptor changePasswordInterceptor() {
		return new ChangePasswordInterceptor();
	}

	@Bean
	public MockInterceptor mockInterceptor() {
		return new MockInterceptor();
	}
}
