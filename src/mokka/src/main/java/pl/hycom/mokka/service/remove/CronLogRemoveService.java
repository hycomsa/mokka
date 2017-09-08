package pl.hycom.mokka.service.remove;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.emulator.logs.LogRepository;

import java.util.Calendar;

@Service
@Slf4j
public class CronLogRemoveService {

    @Autowired
    private LogRepository logRepository;

    @Value("${remove.enabled}")
    private boolean removeEnabled;

    @Value("${remove.days}")
    private int removeOlderThanThatDays;

    @Scheduled(cron = "${remove.cron.expression}")
    public void removeOldMocks() {
        if (!removeEnabled) {
            return;
        } else {

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -removeOlderThanThatDays);

            java.sql.Date outdated = new java.sql.Date(cal.getTimeInMillis());

            logRepository.removeOlderThan(outdated);
            log.debug("Logs older than 30 days have been removed.");
        }
    }
}


