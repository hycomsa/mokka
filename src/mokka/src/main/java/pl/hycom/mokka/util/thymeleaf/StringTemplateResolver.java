package pl.hycom.mokka.util.thymeleaf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
public class StringTemplateResolver extends TemplateResolver {

	private String content;

	public StringTemplateResolver(String content) {
		this.content = content;
		setResourceResolver(new ResourceResolver());
	}

	private class ResourceResolver implements IResourceResolver {

		@Override
		public String getName() {
			return "string resolver";
		}

		@Override
		public InputStream getResourceAsStream(TemplateProcessingParameters arg0, String arg1) {
			return new ByteArrayInputStream(StringTemplateResolver.this.content.getBytes());
		}

	}

}
