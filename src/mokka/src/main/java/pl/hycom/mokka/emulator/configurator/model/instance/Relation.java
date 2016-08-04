package pl.hycom.mokka.emulator.configurator.model.instance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity
public class Relation {

	@Id
	@GeneratedValue
	private Long id;

	@OneToOne
	private Thing thing;

	public Long getId() {
		return id;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}
}
