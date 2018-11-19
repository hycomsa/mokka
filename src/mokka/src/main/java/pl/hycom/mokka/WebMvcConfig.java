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
		registry.addInterceptor(mockInterceptor());
		registry.addInterceptor(changePasswordInterceptor());
	}

	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setDefaultEncoding("utf-8");
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
