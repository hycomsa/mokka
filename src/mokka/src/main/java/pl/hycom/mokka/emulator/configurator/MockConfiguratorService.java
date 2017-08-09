package pl.hycom.mokka.emulator.configurator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.hycom.mokka.emulator.configurator.model.MockConfigurator;
import pl.hycom.mokka.emulator.configurator.model.type.ThingType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
// @Transactional
@Component
public class MockConfiguratorService {

	private static final String KEY_TIMEOUT = "timeout";

    @PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MockConfiguratorRepository mockConfiguratorRepository;

	public List<ThingType> getAllThingTypes() {
		return entityManager.createQuery("SELECT e FROM ThingType e").getResultList();
	}

	public void addThingType(ThingType thingType) {
		entityManager.persist(thingType);
	}

	public void saveTimeout(int timeout) {

		if (mockConfiguratorRepository.existsKey(KEY_TIMEOUT)) {
			mockConfiguratorRepository.updateValueByKey(KEY_TIMEOUT, Integer.toString(timeout));
		} else {

			MockConfigurator configurator = new MockConfigurator();
			configurator.setKeyConfigurator(KEY_TIMEOUT);
			configurator.setValueConfigurator(Integer.toString(timeout));

			mockConfiguratorRepository.save(configurator);
		}
	}

	public int getTimeout() {
		return Integer.parseInt(mockConfiguratorRepository.getValueByKey(KEY_TIMEOUT));
	}

}
