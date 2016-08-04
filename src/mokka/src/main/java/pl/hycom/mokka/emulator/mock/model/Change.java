package pl.hycom.mokka.emulator.mock.model;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pl.hycom.mokka.web.json.JsonJodaDateTimeSerializer;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Data
public class Change {

	@JsonSerialize(using = JsonJodaDateTimeSerializer.class)
	private DateTime date;

	private String author;

	private Map<String, Map<String, Object>> diffs = new HashMap<>();
}
