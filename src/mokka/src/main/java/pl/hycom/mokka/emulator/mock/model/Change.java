package pl.hycom.mokka.emulator.mock.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import pl.hycom.mokka.emulator.mock.TrackChanges;
import pl.hycom.mokka.web.json.JsonJodaDateTimeSerializer;
import pl.hycom.mokka.web.json.View;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Data
public class Change implements Serializable {

	@JsonView(View.General.class)
	@TrackChanges
	private Integer id;

	@JsonSerialize(using = JsonJodaDateTimeSerializer.class)
	private DateTime date;

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
