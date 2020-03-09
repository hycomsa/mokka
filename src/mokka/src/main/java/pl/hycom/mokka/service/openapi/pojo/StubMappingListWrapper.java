package pl.hycom.mokka.service.openapi.pojo;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import java.util.List;

/**
 * Additional layer of abstraction to wrap {@link List} of {@link StubMapping}
 *
 * @author Kamil Adamiec (kamil.adamiec@hycom.pl)
 */
public class StubMappingListWrapper {

    private List<StubMapping> stubMappings;

    public List<StubMapping> getStubMappings() {
        return stubMappings;
    }

    public void setStubMappings(List<StubMapping> stubMappings) {
        this.stubMappings = stubMappings;
    }
}
