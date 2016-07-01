package pl.hycom.mokka.service.file;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Service responsible for file management
 *
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@FunctionalInterface
public interface FileService {
    /**
     * Gets file by given fileName
     * @param fileName  String Searching file name
     * @return File
     * @throws FileNotFoundException
     */
    File fetchFile(String fileName) throws FileNotFoundException;
}
