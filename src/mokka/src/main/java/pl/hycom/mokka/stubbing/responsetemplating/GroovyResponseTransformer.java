package pl.hycom.mokka.stubbing.responsetemplating;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.RequestTemplateModel;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Extends WireMock's {@link ResponseDefinition} transformation for groovy responses.
 *
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@Component
public class GroovyResponseTransformer extends ResponseDefinitionTransformer {

    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource fileSource, Parameters parameters) {

        WireMockContext ctx = new WireMockContext(request);
        Binding binding = new Binding();

        binding.setVariable("ctx", ctx);
        binding.setVariable("request", RequestTemplateModel.from(request));

        String value = StringUtils.EMPTY;
        if (!StringUtils.isBlank(responseDefinition.getBody())) {
            value = new GroovyShell(binding).evaluate(responseDefinition.getBody()).toString();
        }

        return ResponseDefinitionBuilder.like(responseDefinition).but().withBody(value).build();
    }

    @Override
    public String getName() {
        return "groovy-transformer";
    }
}
