package pl.hycom.mokka.emulator.mock.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonView;
import com.univocity.parsers.annotations.Parsed;

import lombok.Data;
import pl.hycom.mokka.web.json.View;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity(name = "ConfigContent")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@Audited
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.WRAPPER_OBJECT, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = GroovyConfigurationContent.class, name = "groovy"),
		@JsonSubTypes.Type(value = StringConfigurationContent.class, name = "string"),
		@JsonSubTypes.Type(value = XmlConfigurationContent.class, name = "xml"),
		@JsonSubTypes.Type(value = ModelConfigurationContent.class, name = "model")
})
@XmlTransient
public abstract class ConfigurationContent {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Parsed
	@JsonView(View.General.class)
	private Long id;

}
