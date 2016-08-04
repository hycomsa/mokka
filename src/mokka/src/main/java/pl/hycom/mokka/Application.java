package pl.hycom.mokka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for spring-boot mokka ui application
 *
 * @author Michal Adamczyk, HYCOM S.A.
 */
@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EnableAsync
@EnableScheduling
public class Application {
    public static ApplicationContext APPLICATION_CONTEXT;

    public static void main(String[] args) {
        APPLICATION_CONTEXT = SpringApplication.run(Application.class, args);
    }

}
