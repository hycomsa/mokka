package pl.hycom.mokka;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public abstract class AbstractTest {

}
