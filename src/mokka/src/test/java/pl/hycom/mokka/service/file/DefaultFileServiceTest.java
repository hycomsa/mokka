package pl.hycom.mokka.service.file;

import org.junit.Assert;
import org.junit.Test;
import pl.hycom.mokka.AbstractTest;

import java.io.File;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public class DefaultFileServiceTest extends AbstractTest {

    DefaultFileService fileService = new DefaultFileService();

    @Test
    public void testFetchFileSuccess() {
        fileService.setSourceDirectory("src/test/resource/");
        File file = fileService.fetchFile("samplefile.txt");
        Assert.assertNotNull(file);
    }

    @Test
    public void testFetchFileFailure() {
        fileService.setSourceDirectory("src/test/resource/");
        File file = fileService.fetchFile("samplefile1.txt");
        Assert.assertNull(file);

        file = fileService.fetchFile(null);
        Assert.assertNull(file);
    }
}
