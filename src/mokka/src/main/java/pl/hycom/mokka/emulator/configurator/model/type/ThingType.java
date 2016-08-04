package pl.hycom.mokka.emulator.configurator.model.type;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity
public class ThingType {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@OneToMany(cascade = CascadeType.ALL)
	private List<FieldType> fields = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<FieldType> getFields() {
		return fields;
	}

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("name", name)
            .add("fields", fields)
            .toString();
    }
}
