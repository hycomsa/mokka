package pl.hycom.mokka.emulator.mock.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.hycom.mokka.emulator.mock.TrackChanges;
import pl.hycom.mokka.web.json.View;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity(name = "StringConfigContent")
@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@JsonTypeName("string")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringConfigurationContent extends ConfigurationContent {

	@Lob
	@JsonView(View.General.class)
	@Basic(fetch = FetchType.EAGER)
	@TrackChanges
	@XmlElement
	private String value;

}
