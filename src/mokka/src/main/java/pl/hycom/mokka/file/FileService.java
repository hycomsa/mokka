package pl.hycom.mokka.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Service responsible for file management
 *
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public interface FileService {
    /**
     * Gets file by given fileName
     *
     * @param fileName
     *         String Searching file name
     * @return File
     * @throws FileNotFoundException
     */
    File fetchFile(String fileName) throws FileNotFoundException;

    /**
     * Method returns List of files names
     *
     * @return List<String> list of files names
     */
    List<String> fetchAllFiles();
}
