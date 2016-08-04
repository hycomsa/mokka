package pl.hycom.mokka.web.json;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public class JsonJodaDateTimeSerializer extends JsonSerializer<DateTime> {

	@Override
	public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		gen.writeNumber(value.toDate().getTime());
	}
}
