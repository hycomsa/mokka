package pl.hycom.mokka.core;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.security.UserManager;

/**
 * Performs additional checks on startup making sure the application has initial configuration done.
 * Currently application initialization is understood as : create application admin.
 *
 * Finally should be removed and replaced with application setup wizard.
 *
 * Setup may be disabled with `setup.initial.enabled` property.
 *
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitialSetupCommandLineRunner implements CommandLineRunner {

    private final UserManager userManager;

    @Setter
    @Value("${setup.initial.enabled}")
    private boolean initialSetupEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (!initialSetupEnabled) {
            log.debug("Initial setup disabled.");
            return;
        }

        log.info("Check if initial setup is required.");

        if (userManager.numberOfAdmins() == 0) {
            log.info("Initial setup is required. Creating administrator.");
            userManager.createAdminUser();
            log.info("Initial setup done.");

            return;
        }

        log.warn("Initial setup skipped as seems to be already done. Disable this step with: setup.initial.enabled=false");
    }
}
