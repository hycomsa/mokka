package pl.hycom.mokka.service.file;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@PropertySource("classpath:application.properties")
@Component
public class DefaultFileService implements FileService {
    private static final Logger LOG = Logger.getLogger(DefaultFileService.class);
    @Value("${file.download.directory}")
    private String sourceDirectory;

    @Override
    public File fetchFile(String fileName) {
        LOG.debug("Invoking DefaultFileService#fetchFile with argument [" + fileName + "]");
        if (StringUtils.isEmpty(fileName)) {
            LOG.warn("File name cannot be empty");
            return null;
        }
        String fullPath = sourceDirectory + fileName;

        File file = new File(fullPath);
        if (!file.exists()) {
            LOG.warn("File with path [" + fullPath + "] does not exist");
            return null;
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
