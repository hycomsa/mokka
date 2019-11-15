package pl.hycom.mokka.service.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public class DefaultFileServiceTest {

    private DefaultFileService fileService;

    @BeforeEach
    public void init() {
      fileService = new DefaultFileService();
    }

    @Test
    public void testFetchFileSuccess() throws FileNotFoundException {
        fileService.setSourceDirectory("src/test/resources/files/");
        File file = fileService.fetchFile("samplefile.txt");
        Assertions.assertNotNull(file);
    }

    @Test
    public void testFetchFileFileNotFoundException() throws FileNotFoundException {
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            fileService.setSourceDirectory("src/test/resources/files/");
            fileService.fetchFile("samplefile1.txt");
        });
    }

    @Test
    public void testFetchFileIllegalArgumentException() throws FileNotFoundException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileService.fetchFile(null));

    }

    @Test
    public void testFetchAllFileSuccess() {
        fileService.setSourceDirectory("src/test/resources/files/");
        List<String> files = fileService.fetchAllFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.size());
    }

    @Test
    public void testFetchAllFileWithoutFiles() {
        fileService.setSourceDirectory("src/test/resources/files/dummy-directory/");
        List<String> files = fileService.fetchAllFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(0, files.size());
    }

}
