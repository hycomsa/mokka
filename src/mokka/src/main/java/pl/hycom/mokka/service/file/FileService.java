package pl.hycom.mokka.service.file;

import java.io.File;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@FunctionalInterface
public interface FileService {
    File fetchFile(String fileName);
}
