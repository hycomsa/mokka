package pl.hycom.mokka.emulator.configurator.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Entity
public class MockConfigurator {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String keyConfigurator;

	private String valueConfigurator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKeyConfigurator() {
		return keyConfigurator;
	}

	public void setKeyConfigurator(String keyConfigurator) {
		this.keyConfigurator = keyConfigurator;
	}

	public String getValueConfigurator() {
		return valueConfigurator;
	}

	public void setValueConfigurator(String valueConfigurator) {
		this.valueConfigurator = valueConfigurator;
	}

}
