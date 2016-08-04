package pl.hycom.mokka.emulator.configurator.model.type;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity
public class FieldType {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String relationTo;

	private boolean collection;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelationTo() {
		return relationTo;
	}

	public void setRelationTo(String relationTo) {
		this.relationTo = relationTo;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("name", name)
            .add("relationTo", relationTo)
            .add("collection", collection)
            .toString();
    }
}
