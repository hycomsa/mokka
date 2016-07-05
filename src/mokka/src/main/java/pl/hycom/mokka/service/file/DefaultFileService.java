package pl.hycom.mokka.service.file;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */

/**
 * Service responsible for file management, implements FileService interface
 */
@Service
public class DefaultFileService implements FileService {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DefaultFileService.class);
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
        LOG.debug("Invoking DefaultFileService#fetchFile with argument [" + fileName + "]");
        checkArgument(!StringUtils.isEmpty(fileName), "File name cannot be empty");
        String fullPath = sourceDirectory + fileName;

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new FileNotFoundException("File with path [" + fullPath + "] does not exist");
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
