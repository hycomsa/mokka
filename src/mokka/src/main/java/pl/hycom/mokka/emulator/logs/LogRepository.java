package pl.hycom.mokka.emulator.logs;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;
import pl.hycom.mokka.emulator.logs.model.Log;

import javax.transaction.Transactional;
import java.util.Set;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Transactional
public interface LogRepository extends CrudRepository<Log, Long> {

    @Query("SELECT DISTINCT l FROM LogsMocks l ORDER BY l.uri ASC")
    Set<Log> findUniqueLogs();

    @Modifying
    @Transactional
    @Query("DELETE FROM LogsMocks l WHERE l.date < :outdated")
    int removeOlderThan(@Param("outdated") java.sql.Date date);
}
