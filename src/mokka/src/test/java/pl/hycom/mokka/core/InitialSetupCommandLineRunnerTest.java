package pl.hycom.mokka.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.hycom.mokka.security.UserManager;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Piotr Kulasek-Szwed <piotr.kulasek-szwed@hycom.pl>
 */
@ExtendWith(MockitoExtension.class)
public class InitialSetupCommandLineRunnerTest {

    @Mock
    private UserManager userManager;

    @InjectMocks
    private InitialSetupCommandLineRunner initialSetupCommandLineRunner;

    @Test
    public void shouldSkipInitialConfig(){
        // given
        when(userManager.numberOfAdmins()).thenReturn(1);
        initialSetupCommandLineRunner.setInitialSetupEnabled(true);

        // when
        try {
            initialSetupCommandLineRunner.run("");
        } catch (Exception e) {
            Assertions.fail();
        }

        // then
        verify(userManager, atLeastOnce()).numberOfAdmins();
    }

    @Test
    public void shouldExecuteInitialConfig(){
        // given
        when(userManager.numberOfAdmins()).thenReturn(0);
        initialSetupCommandLineRunner.setInitialSetupEnabled(true);

        // when
        try {
            initialSetupCommandLineRunner.run("");
        } catch (Exception e) {
            Assertions.fail();
        }

        // then
        verify(userManager, atLeastOnce()).createAdminUser();
    }

}
