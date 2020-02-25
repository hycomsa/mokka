package pl.hycom.mokka.endpoint;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.expect;


/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@DirtiesContext
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class FileControllerIT {
    @Value("${local.server.port}")
    protected int serverPort;

    @BeforeEach
    public void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    public void testFetchFile() {
        expect().statusCode(200).when().get("/files/samplefile.txt");
        expect().statusCode(200).when().get("/files/samplefile.txt/?content-disposition=inline");
    }

    @Test
    public void testFetchFileHttpNotFound() {
        expect().statusCode(404).when().get("/files/mozilladd.pdf");
    }

    @Test
    public void testFetchAllFiles() {
        expect().statusCode(200).body("", Matchers.notNullValue()).when().get("/files");
    }
}
