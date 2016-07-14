package pl.hycom.mokka.emulator.configurator.model.instance;

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
public class Thing {

	@Id
	@GeneratedValue
	private Long id;

	private String type;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Field> fields = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Field> getFields() {
		return fields;
	}
}
