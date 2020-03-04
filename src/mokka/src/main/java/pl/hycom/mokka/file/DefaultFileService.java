package pl.hycom.mokka.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Service responsible for file management, implements FileService interface
 *
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@Slf4j
@Service
class DefaultFileService implements FileService {
    /**
     * Location for files
     */
    @Value("${file.download.directory}")
    private String sourceDirectory;

    /**
     * Gets file by given fileName contained in configured directory, if file doesn't exist throws FileNotFoundException
     *
     * @param fileName
     *         String Searching file name
     * @return File
     * @throws FileNotFoundException,IllegalArgumentException
     */
    @Override
    public File fetchFile(String fileName) throws FileNotFoundException {
        log.debug("Invoking DefaultFileService#fetchFile with argument [{}]", fileName);
        checkArgument(!StringUtils.isEmpty(fileName), "File name cannot be empty");
        String fullPath = sourceDirectory + fileName;

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new FileNotFoundException("File with path [" + fullPath + "] does not exist");
        }
        log.debug("Found file [{}]", file.getAbsolutePath());
        log.debug("Ending DefaultFileService#fetchFile");
        return file;
    }

    /**
     * Method returns List of files names existing in configured directory,
     * if there is no file returns an empty list
     *
     * @return List<String> list of files names
     */
    @Override
    public List<String> fetchAllFiles() {

        return fetchAllFilesInternal(sourceDirectory);
    }

    private List<String> fetchAllFilesInternal(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        List<String> files = new ArrayList<>();
        if (!ArrayUtils.isEmpty(listOfFiles)) {
            for (File item : listOfFiles) {
                if (item.isFile()) {
                    files.add(item.getName());
                }
            }
        }
        return files;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }
}
