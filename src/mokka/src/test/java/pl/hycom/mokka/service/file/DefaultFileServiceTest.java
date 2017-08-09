package pl.hycom.mokka.service.file;

import org.junit.Assert;
import org.junit.Test;
import pl.hycom.mokka.AbstractTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public class DefaultFileServiceTest extends AbstractTest {

    DefaultFileService fileService = new DefaultFileService();

    @Test
    public void testFetchFileSuccess() throws FileNotFoundException {
        fileService.setSourceDirectory("src/test/resources/files/");
        File file = fileService.fetchFile("samplefile.txt");
        Assert.assertNotNull(file);
    }

    @Test(expected = FileNotFoundException.class)
    public void testFetchFileFileNotFoundException() throws FileNotFoundException {
        fileService.setSourceDirectory("src/test/resources/files/");
        fileService.fetchFile("samplefile1.txt");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchFileIllegalArgumentException() throws FileNotFoundException {
        fileService.fetchFile(null);

    }

    @Test
    public void testFetchAllFileSuccess() {
        fileService.setSourceDirectory("src/test/resources/files/");
        List<String> files = fileService.fetchAllFiles();
        Assert.assertNotNull(files);
        Assert.assertEquals(1, files.size());
    }

    @Test
    public void testFetchAllFileWithoutFiles() {
        fileService.setSourceDirectory("src/test/resources/files/dummy-directory/");
        List<String> files = fileService.fetchAllFiles();
        Assert.assertNotNull(files);
        Assert.assertEquals(0, files.size());
    }

}
