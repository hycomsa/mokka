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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Extends WireMock's {@link ResponseDefinition} transformation for groovy responses.
 *
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
@Slf4j
public class GroovyResponseTransformer extends ResponseDefinitionTransformer {

    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource fileSource, Parameters parameters) {
        log.debug("Initial response body [{}]", responseDefinition.getBody());
        if (StringUtils.isBlank(responseDefinition.getBody())) {
            return ResponseDefinitionBuilder.like(responseDefinition).but().withBody(StringUtils.EMPTY).build();
        }

        WireMockContext ctx = new WireMockContext(request);
        Binding binding = new Binding();

        binding.setVariable("ctx", ctx);
        binding.setVariable("request", RequestTemplateModel.from(request));
        log.debug("Binding variables [{}]", binding.getVariables());

        String value = resolveValue(binding,responseDefinition.getBody());
        log.debug("Response body after Groovy Shell binding [{}]", value);

        return ResponseDefinitionBuilder.like(responseDefinition).but().withBody(value).build();
    }

    private String resolveValue(Binding binding, String body) {
        try{
            return new GroovyShell(binding).evaluate(body).toString();
        }
        catch(Exception e){
            return body;
        }
    }

    @Override
    public String getName() {
        return "groovy-transformer";
    }
}
