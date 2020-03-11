package pl.hycom.mokka.emulator.mock;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import java.util.Set;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public interface MockConfigurationRepository extends RevisionRepository<MockConfiguration, Long, Integer>, CrudRepository<MockConfiguration, Long> {

	@Query("SELECT DISTINCT m.path FROM MockConfig m ORDER BY LOWER(m.path) ASC")
	Set<String> findUniquePaths();

	Iterable<MockConfiguration> findAllByEnabledIsTrue();

}
