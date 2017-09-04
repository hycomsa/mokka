package pl.hycom.mokka.emulator.mock;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;

import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public interface MockConfigurationRepository extends RevisionRepository<MockConfiguration, Long, Integer>, CrudRepository<MockConfiguration, Long> {

	@Query("SELECT DISTINCT m.path FROM MockConfig m ORDER BY m.path ASC")
	Set<String> findUniquePaths();

}
