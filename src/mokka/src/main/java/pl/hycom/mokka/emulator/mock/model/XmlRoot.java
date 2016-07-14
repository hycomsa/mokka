package pl.hycom.mokka.emulator.mock.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

import java.util.List;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Data
@XmlRootElement(name = "mocks")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlRoot {

	@XmlElement
	private List<MockConfiguration> mocks;
}
