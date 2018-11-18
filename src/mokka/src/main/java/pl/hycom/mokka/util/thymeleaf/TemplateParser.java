package pl.hycom.mokka.util.thymeleaf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import pl.hycom.mokka.Application;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Component
public class TemplateParser {

	@Autowired
	private ServletContext servletContext;

	public String parse(String content, HttpServletRequest request, HttpServletResponse response) {
		return parse(content, new HashMap<>(), request, response);
	}

	public String parse(String content, Map<String, Object> variables, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isBlank(content)) {
			return content;
		}

		IContext context = new WebEngineContext(request, response, servletContext, Locale.getDefault(), variables, Application.APPLICATION_CONTEXT);

		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(new StringTemplateResolver());

		return engine.process(Integer.toString(content.hashCode()), context);
	}

}
