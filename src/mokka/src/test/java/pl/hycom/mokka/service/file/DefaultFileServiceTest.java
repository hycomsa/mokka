package pl.hycom.mokka.service.file;

import org.junit.Assert;
import org.junit.Test;
import pl.hycom.mokka.AbstractTest;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public class DefaultFileServiceTest extends AbstractTest {

    DefaultFileService fileService = new DefaultFileService();

    @Test
    public void testFetchFileSuccess() throws FileNotFoundException {
        fileService.setSourceDirectory("src/test/resource/");
        File file = fileService.fetchFile("samplefile.txt");
        Assert.assertNotNull(file);
    }

    @Test(expected = FileNotFoundException.class)
    public void testFetchFileFailure() throws FileNotFoundException {
        fileService.setSourceDirectory("src/test/resource/");

        File file = fileService.fetchFile("samplefile1.txt");
        Assert.assertNull(file);

        file = fileService.fetchFile(null);
        Assert.assertNull(file);
    }
}