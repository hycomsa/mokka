package pl.hycom.mokka.emulator.logs.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.hycom.mokka.emulator.logs.LogStatus;
import pl.hycom.mokka.web.json.View;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Data
@Entity(name = "LogsMocks")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Log implements Serializable {

	private static final long serialVersionUID = -8366702217305949147L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(View.General.class)
	private Long id;

	@JsonView(View.General.class)
	private String uri;

	@JsonView(View.General.class)
	private String httpMethod;

	@Lob
	@JsonView(View.Detailed.class)
	private String request;

	@Lob
	@JsonView(View.Detailed.class)
	private String response;

	@Column(name = "log_timestamp")
	@JsonView(View.General.class)
	private Timestamp date;

	@Column(name = "log_status")
	@JsonView(View.General.class)
	private LogStatus status;

	@Column(name = "log_from")
	@JsonView(View.General.class)
	private String from;

	@JsonView(View.General.class)
	private Long configurationId;

    @JsonView(View.General.class)
    private String name;
}
