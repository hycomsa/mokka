package pl.hycom.mokka.emulator.mock.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import pl.hycom.mokka.emulator.mock.TrackChanges;
import pl.hycom.mokka.web.json.View;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Data
public class Change implements Serializable {

	@JsonView(View.General.class)
	@TrackChanges
	private Integer id;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime date;

	private String author;

	private Map<String, Map<String, Object>> diffs = new HashMap<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Map<String, Map<String, Object>> getDiffs() {
		return diffs;
	}
}
