package pl.hycom.mokka.service.file;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */

/**
 * Service responsible for file management, implements FileService interface
 */
@Component
public class DefaultFileService implements FileService {
    private static final Logger LOG = Logger.getLogger(DefaultFileService.class);
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
     * @throws FileNotFoundException
     */
    @Override
    public File fetchFile(String fileName) throws FileNotFoundException {
        LOG.debug("Invoking DefaultFileService#fetchFile with argument [" + fileName + "]");
        if (StringUtils.isEmpty(fileName)) {
            LOG.warn("File name cannot be empty");
            throw new FileNotFoundException();
        }
        String fullPath = sourceDirectory + fileName;

        File file = new File(fullPath);
        if (!file.exists()) {
            LOG.warn("File with path [" + fullPath + "] does not exist");
            throw new FileNotFoundException();
        }
        LOG.debug("Found file [" + file.getAbsolutePath() + "]");
        LOG.debug("Ending DefaultFileService#fetchFile");
        return file;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }
}
