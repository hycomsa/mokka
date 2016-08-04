package pl.hycom.mokka.emulator.configurator.model.instance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity
public class Field {

	@Id
	@GeneratedValue
	private Long id;

	@OneToOne
	private Relation relation;

	private String name;

	private String value;

	public Long getId() {
		return id;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if (value instanceof Relation) {
			relation = (Relation) value;
		} else {
			this.value = String.valueOf(value);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
