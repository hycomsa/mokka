package pl.hycom.mokka.emulator.configurator;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pl.hycom.mokka.emulator.configurator.model.MockConfigurator;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Transactional
public interface MockConfiguratorRepository extends CrudRepository<MockConfigurator, Long> {

	@Query("SELECT c.valueConfigurator FROM MockConfigurator c WHERE c.keyConfigurator =:key")
	String getValueByKey(@Param("key") String key);

	@Modifying
	@Query("UPDATE MockConfigurator c SET c.valueConfigurator = :value WHERE c.keyConfigurator = :key")
	void updateValueByKey(@Param("key") String key, @Param("value") String value);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM MockConfigurator c WHERE c.keyConfigurator = :key")
	boolean existsKey(@Param("key") String key);
}
