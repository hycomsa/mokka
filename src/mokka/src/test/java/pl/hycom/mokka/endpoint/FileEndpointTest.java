package pl.hycom.mokka.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.hycom.mokka.AbstractTest;
import pl.hycom.mokka.service.file.FileService;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public class FileEndpointTest extends AbstractTest {
    private static final String FILE_NAME = "file.txt";
    private static final String FILE_ID = "file";
    private static final String FILE_EXT="txt";
    @InjectMocks
    FileEndpoint fileEndpoint = new FileEndpoint();
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FileService fileService;
    @Mock
    ServletContext context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFetchFile() throws IOException {
        File file = new File("src/test/resource/samplefile.txt");
        Mockito.when(fileService.fetchFile(FILE_NAME)).thenReturn(file);
        Mockito.when(request.getServletContext()).thenReturn(context);
        ServletOutputStream outputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {

            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }
        };
        Mockito.when(response.getOutputStream()).thenReturn(outputStream);
        fileEndpoint.fetchFile(FILE_ID,FILE_EXT, response, request);

        Mockito.when(fileService.fetchFile(FILE_ID)).thenReturn(null);

        fileEndpoint.fetchFile(FILE_NAME,null, response, request);
    }

}
