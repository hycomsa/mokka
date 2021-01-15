package pl.hycom.mokka.stubbing.responsetemplating;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import static com.github.tomakehurst.wiremock.http.HttpHeader.httpHeader;

/**
 * @author Dominik Gorkiewicz (dominik.gorkiewicz@hycom.pl)
 */
@Slf4j
public class GzipResponseTransformer extends ResponseTransformer {

    private static final String CONTENT_ENCODING = "Content-Encoding";

    private static final String GZIP = "gzip";


    @SneakyThrows
    @Override
    public Response transform(

        Request request, Response response, FileSource fileSource, Parameters parameters) {
        HttpHeaders headers = response.getHeaders();

        if (headers.keys().stream().anyMatch(CONTENT_ENCODING::equals) && headers.getHeader(CONTENT_ENCODING)
            .containsValue(GZIP)) {
            log.info("Decompressing gzip response body.");
            return Response.Builder.like(response).but()
                .body(decompress(response.getBody()).getBytes(StandardCharsets.UTF_8)).but()
                .headers(headers.plus(httpHeader(CONTENT_ENCODING, "identity"))).build();
        } else {
            return response;
        }
    }

    private String decompress(final byte[] compressed) throws IOException {

        if ((compressed == null) || (compressed.length == 0)) {
            throw new IllegalArgumentException("Cannot unzip null or empty bytes.");
        }
        if (!isCompressed(compressed)) {
            return new String(compressed);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        output.append(line);
                    }
                    return output.toString();
                }
            }
        }
    }

    private boolean isCompressed(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    @Override
    public String getName() {
        return "gzip-response-transformer";
    }
}
