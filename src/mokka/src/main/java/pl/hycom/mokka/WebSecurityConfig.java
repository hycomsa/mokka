package pl.hycom.mokka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import pl.hycom.mokka.security.ServiceWebSecurityConfigurer;

import java.util.List;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired(required = false)
    private List<ServiceWebSecurityConfigurer> serviceSecConfigs;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

	    if (serviceSecConfigs != null) {
	        for (ServiceWebSecurityConfigurer securityConfigurer: serviceSecConfigs) {
                securityConfigurer.configure(http);
            }
        }

        // mgmt console
        http.authorizeRequests().antMatchers("/", "/configurations/**", "/logs/**", "/change-password/**", "/users/**").authenticated().and()
            .authorizeRequests().antMatchers("/**").permitAll()
            .and()
				.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
				.logout()
				.permitAll();
	}

	@Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/js/**", "/css/**");
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringSecurityDialect springSecurityDialect() {
		return new SpringSecurityDialect();
	}

}
