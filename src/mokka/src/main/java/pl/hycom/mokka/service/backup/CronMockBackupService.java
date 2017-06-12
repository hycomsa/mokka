package pl.hycom.mokka.service.backup;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.emulator.mock.MockConfigurationImportExportManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * File based, mock backup service with configurable (by cron expression) execution times.
 *
 * @author Tomasz Nitecki <tnnn@tnnn.pl>
 */
@Service
@Slf4j
public class CronMockBackupService {

    @Autowired
    private MockConfigurationImportExportManager mockCfgImportExportManager;

    @Value("${backup.enabled}")
    private boolean backupEnabled;
    @Value("${backup.path}")
    private String backupPath;
    @Value("${backup.timestamp.pattern}")
    private String backupTimestampPattern;

    @Scheduled(cron = "${backup.cron.expression}")
    public void createBackup() {
        if (!backupEnabled) return;
        try {
            File csvFile = mockCfgImportExportManager.createExportFile();
            if (csvFile != null) {
                File zipFile = mockCfgImportExportManager.zipFile(csvFile);
                if (zipFile != null) {
                    Files.write(generatePathForBackup(), Files.readAllBytes(zipFile.toPath()));
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private Path generatePathForBackup() {
        return Paths.get(backupPath
                + DateFormatUtils.format(new Date(), backupTimestampPattern)
                + "-mock-config-backup.zip");
    }
}
