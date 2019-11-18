package pl.hycom.mokka;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Configuration
@EnableJpaRepositories(
    repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class
)
public class RepositoryConfiguration {
}
