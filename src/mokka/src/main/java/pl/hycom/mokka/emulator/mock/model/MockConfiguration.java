package pl.hycom.mokka.emulator.mock.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.envers.Audited;
import pl.hycom.mokka.emulator.mock.TrackChanges;
import pl.hycom.mokka.web.json.View;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Data
@Entity(name = "MockConfig")
@Audited
@XmlAccessorType(XmlAccessType.FIELD)
public class MockConfiguration implements Serializable {

	private static final long serialVersionUID = 5405728118174891562L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(View.General.class)
	@XmlAttribute
	private Long id;

	@Column(nullable = false)
	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private String path;

	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private String description;

	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private String pattern;

	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private String httpMethod;

	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private String name;

	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private boolean enabled = true;

	@Column(name = "mock_timeout", nullable = false)
	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private int timeout = 0;

	@Column(name = "mock_order", nullable = false)
	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private int order = 0;

	@JsonView(View.General.class)
	@XmlElement
	@TrackChanges
	private int status = 200;

	@JsonView(View.Detailed.class)
	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@TrackChanges
	private ConfigurationContent configurationContent;

	@XmlAttribute
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

}
