package pl.hycom.mokka.endpoint;

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.hycom.mokka.AbstractTest;
import pl.hycom.mokka.exception.ExceptionHandlingController;
import pl.hycom.mokka.service.file.FileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.jayway.restassured.RestAssured.expect;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@WebIntegrationTest(randomPort = true)
public class FileControllerTest extends AbstractTest {
    private static final String FILE_NAME = "file.txt";
    private static final String FILE_ID = "file";
    private static final String FILE_EXT = "txt";
    @Autowired
    @InjectMocks
    private FileController fileController;

    @Mock
    private FileService fileService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFetchFile() throws IOException {
        File file = new File("src/test/resource/samplefile.txt");
        Mockito.when(fileService.fetchFile(FILE_NAME))
                .thenReturn(file);

        fileController.fetchFile(FILE_ID, FILE_EXT);


    }


    @Test(expected = FileNotFoundException.class)
    public void testFetchFileFailure() throws IOException {

        Mockito.when(fileService.fetchFile(FILE_NAME))
                .thenThrow(FileNotFoundException.class);

        fileController.fetchFile(FILE_ID, FILE_EXT);
    }

    @Test
    public void testFetchFileHttpNotFound() throws IOException {
      /*  RestAssuredMockMvc.mockMvc = MockMvcBuilders.standaloneSetup(fileController)
                .setControllerAdvice(exceptionHandlingController)
                .build();
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);*/

        expect().statusCode(404)
                .when()
                .get("/files/mozilladd?ext=pdf");
    }

}
