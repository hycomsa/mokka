package pl.hycom.mokka.emulator.mock.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity(name = "ModelConfigContent")
@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@JsonTypeName("model")
public class ModelConfigurationContent extends ConfigurationContent {

    @Override
    public String getValue() {
        return StringUtils.EMPTY;
    }
}
