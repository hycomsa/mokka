package pl.hycom.mokka.emulator.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.hycom.mokka.emulator.mock.model.JmsMockConfiguration;

/**
 * @author Tomasz Wozniak (tomasz.wozniak@hycom.pl)
 */
@Slf4j
@RestController
@RequestMapping(headers = "x-requested-with=XMLHttpRequest")
public class JmsMockConfigurationController {

    @Autowired
    JmsMockConfigurationManager configurationManager;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @RequestMapping(value = "/jmsconfiguration/{name}", method = RequestMethod.GET)
    public JmsMockConfiguration sayHello(@PathVariable("name") String name) {
        return configurationManager.hello(name);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @RequestMapping(value = "/jmsconfiguration/list", method = RequestMethod.POST)
    public JmsMockConfiguration listJmsMocks() {
        return configurationManager.returnSampleJmsMock();
    }


}