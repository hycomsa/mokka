package pl.hycom.mokka.emulator.configurator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.hycom.mokka.emulator.configurator.model.type.ThingType;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@RestController
@RequestMapping(path = "/configurator/types")
public class ConfiguratorController {

	@Autowired
	private MockConfiguratorService configuratorService;

	@RequestMapping(path = "/thing-types", method = RequestMethod.GET)
	public Object getAllThingTypes() {
		return configuratorService.getAllThingTypes();
	}

	@RequestMapping(path = "/thing-types", method = RequestMethod.PUT)
	public void addThingType(@RequestBody ThingType thingType) {
		configuratorService.addThingType(thingType);
	}

	@RequestMapping(path = "/thing-types/{id}", method = RequestMethod.POST)
	public Object updateThingType() {

		return null;
	}

	@RequestMapping(path = "/thing-types/{id}", method = RequestMethod.DELETE)
	public Object deleteThingType() {
		// sprawdzic czy nie ma powiazan!

		return null;
	}

	@RequestMapping(path = "/saveTimeout", method = RequestMethod.POST)
	public void saveTimeout(@RequestBody int timeout) {
		configuratorService.saveTimeout(timeout);
	}

}
