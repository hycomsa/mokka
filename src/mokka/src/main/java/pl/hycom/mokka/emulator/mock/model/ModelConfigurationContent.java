package pl.hycom.mokka.emulator.mock.model;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity(name = "ModelConfigContent")
@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@JsonTypeName("model")
public class ModelConfigurationContent extends ConfigurationContent {

}
