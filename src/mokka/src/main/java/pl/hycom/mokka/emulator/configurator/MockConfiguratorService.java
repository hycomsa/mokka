package pl.hycom.mokka.emulator.configurator;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.hycom.mokka.emulator.configurator.model.LocalData;
import pl.hycom.mokka.emulator.configurator.model.MockConfigurator;
import pl.hycom.mokka.emulator.configurator.model.type.ThingType;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
// @Transactional
@Component
public class MockConfiguratorService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MockConfiguratorRepository mockConfiguratorRepository;

	@Autowired
	private LocalData localData;

	public List<ThingType> getAllThingTypes() {
		return entityManager.createQuery("SELECT e FROM ThingType e").getResultList();
	}

	public void addThingType(ThingType thingType) {
		entityManager.persist(thingType);
	}

	public void saveTimeout(int timeout) {

		if (mockConfiguratorRepository.existsKey("timeout")) {
			mockConfiguratorRepository.updateValueByKey("timeout", Integer.toString(timeout));
		} else {

			MockConfigurator configurator = new MockConfigurator();
			configurator.setKeyConfigurator("timeout");
			configurator.setValueConfigurator(Integer.toString(timeout));

			mockConfiguratorRepository.save(configurator);

			// localData.setTimeout(timeout);
		}
	}

	public int getTimeout() {

		return Integer.parseInt(mockConfiguratorRepository.getValueByKey("timeout"));

		// return localData.getTimeout();
	}

}
