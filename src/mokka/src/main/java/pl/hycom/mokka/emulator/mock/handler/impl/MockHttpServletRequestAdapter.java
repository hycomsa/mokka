package pl.hycom.mokka.emulator.mock.handler.impl;

import com.github.tomakehurst.wiremock.common.Encoding;
import com.github.tomakehurst.wiremock.common.Urls;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.http.Cookie;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.QueryParameter;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.multipart.PartParser;
import com.github.tomakehurst.wiremock.jetty9.JettyUtils;
import org.apache.commons.lang3.StringUtils;
import pl.hycom.mokka.emulator.mock.MockContext;
import wiremock.com.google.common.base.Function;
import wiremock.com.google.common.base.MoreObjects;
import wiremock.com.google.common.base.Optional;
import wiremock.com.google.common.collect.FluentIterable;
import wiremock.com.google.common.collect.ImmutableList;
import wiremock.com.google.common.collect.ImmutableMultimap;
import wiremock.com.google.common.collect.Lists;
import wiremock.com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link Request} initialized with {@link MockContext} and {@link HttpServletRequest}.
 *
 * @author Bartosz Kuron (bartosz.kuron@hycom.pl)
 */
public class MockHttpServletRequestAdapter implements Request {

    MockContext mockContext;
    HttpServletRequest request;

    public MockHttpServletRequestAdapter(MockContext mockContext) {
        this.mockContext = mockContext;
        this.request = mockContext.getRequest();
    }

    @Override
    public String getUrl() {
        if (request.getRequestURL() != null) {
            return request.getRequestURL().toString();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getAbsoluteUrl() {
        return request.getRequestURL() + (StringUtils.isBlank(this.request.getQueryString()) ? "" : "?" + this.request.getQueryString());
    }

    @Override
    public RequestMethod getMethod() {

        return RequestMethod.fromString(mockContext.getHttpMethod().toUpperCase());
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getHost() {
        return request.getServerName();
    }

    @Override
    public int getPort() {
        return request.getServerPort();
    }

    @Override
    public String getClientIp() {
        return mockContext.getFrom();
    }

    @Override
    public String getHeader(String key) {
        return request.getHeader(key);
    }

    @Override
    public HttpHeader header(String key) {
        List<String> valueList = Collections.list(this.request.getHeaders(key));
        return new HttpHeader(key, valueList);
    }

    @Override
    public ContentTypeHeader contentTypeHeader() {
        return this.getHeaders().getContentTypeHeader();
    }

    @Override
    public HttpHeaders getHeaders() {
        List<HttpHeader> headerList = Lists.newArrayList();

        for (String key : this.getAllHeaderKeys()) {
            headerList.add(this.header(key));
        }

        return new HttpHeaders(headerList);
    }

    @Override
    public boolean containsHeader(String key) {
        return this.header(key).isPresent();
    }

    @Override
    public Set<String> getAllHeaderKeys() {
        LinkedHashSet<String> headerKeys = new LinkedHashSet();
        Enumeration headerNames = this.request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            headerKeys.add(headerNames.nextElement().toString());
        }

        return headerKeys;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
        javax.servlet.http.Cookie[] cookies = MoreObjects.firstNonNull(this.request.getCookies(), new javax.servlet.http.Cookie[0]);

        for (javax.servlet.http.Cookie cookie : cookies) {
            builder.put(cookie.getName(), cookie.getValue());
        }

        return Maps.transformValues(builder.build().asMap(), (Function<Collection<String>, Cookie>) input -> {
            assert input != null;
            return new Cookie((String) null, ImmutableList.copyOf(input));
        });
    }

    @Override
    public QueryParameter queryParameter(String key) {
        return MoreObjects.firstNonNull(Urls.splitQuery(this.request.getQueryString()).get(key), QueryParameter.absent(key));

    }

    @Override
    public byte[] getBody() {
        return mockContext.getRequestBody().getBytes();
    }

    @Override
    public String getBodyAsString() {
        return mockContext.getRequestBody();
    }

    @Override
    public String getBodyAsBase64() {
        return Encoding.encodeBase64(this.getBody());
    }

    @Override
    public boolean isMultipart() {
        String header = this.getHeader("Content-Type");
        return header != null && header.contains("multipart/form-data");
    }

    @Override
    public Collection<Part> getParts() {
        if (!this.isMultipart()) {
            return null;
        } else {
            return PartParser.parseFrom(this);
        }
    }

    @Override
    public Part getPart(String name) {
        if (name != null && name.length() != 0) {
            return FluentIterable.from(getParts()).firstMatch(input -> name.equals(input.getName())).get();
        } else {
            return null;
        }
    }

    @Override
    public boolean isBrowserProxyRequest() {
        if (!JettyUtils.isJetty()) {
            return false;
        } else if (this.request instanceof wiremock.org.eclipse.jetty.server.Request) {
            wiremock.org.eclipse.jetty.server.Request jettyRequest = (wiremock.org.eclipse.jetty.server.Request) this.request;
            return JettyUtils.getUri(jettyRequest).isAbsolute();
        } else {
            return false;
        }
    }

    @Override
    public Optional<Request> getOriginalRequest() {
        Request originalRequest = (Request) this.request.getAttribute("wiremock.ORIGINAL_REQUEST");
        return Optional.fromNullable(originalRequest);
    }
}
