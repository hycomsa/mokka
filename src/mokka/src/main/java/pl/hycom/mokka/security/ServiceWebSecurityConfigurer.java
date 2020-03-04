package pl.hycom.mokka.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Allows to provide {@link HttpSecurity} configuration in modular way.
 * Just create implementation specific for your use and register it as a Spring Bean.
 * Later on any implementation of the
 * {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(HttpSecurity)}
 * should autowire the implementations and use them in WebSecurityConfigurerAdapter#configure(HttpSecurity).
 *
 * As for example see {@link pl.hycom.mokka.WebSecurityConfig}
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
public interface ServiceWebSecurityConfigurer {

    void configure(HttpSecurity http) throws Exception;

}
