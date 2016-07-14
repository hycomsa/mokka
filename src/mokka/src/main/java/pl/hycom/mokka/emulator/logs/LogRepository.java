package pl.hycom.mokka.emulator.logs;

import org.springframework.data.repository.CrudRepository;

import pl.hycom.mokka.emulator.logs.model.Log;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public interface LogRepository extends CrudRepository<Log, Long> {

}
