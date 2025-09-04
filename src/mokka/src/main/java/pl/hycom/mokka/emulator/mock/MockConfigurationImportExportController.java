package pl.hycom.mokka.emulator.mock;

import com.google.common.collect.ImmutableMap;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Controller
@Slf4j
public class MockConfigurationImportExportController {

    private static final String STATUS = "status";

    @Autowired
    private pl.hycom.mokka.emulator.mock.MockConfigurationImportExportManager importExportManager;

    @Autowired
    private MockConfigurationManager mockConfigurationManager;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EDITOR')")
    @GetMapping(value = "/configurations/export")
    public Object export() {

        byte[] documentBody = new byte[0];
        try {
            File csvFile = importExportManager.createExportFile();
            if (csvFile != null) {
                File zipFile = importExportManager.zipFile(csvFile);
                if (zipFile != null) {
                    documentBody = Files.readAllBytes(zipFile.toPath());
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "zip"));
        header.setContentDispositionFormData("attachment", "mock-configuration-export-" + DateFormatUtils
                .format(new Date(), "yyyy-MM-dd-HH-mm-ss-SSS") + ".zip");
        header.setContentLength(documentBody.length);

        return new HttpEntity<byte[]>(documentBody, header);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/configurations/all")
    @ResponseBody
    public Object deleteAll() {
        log.info("Request to delete all mock configurations received.");
        mockConfigurationManager.deleteAll();
        return ImmutableMap.of(STATUS, "ok");
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping(value = "/configurations/import")
    @ResponseBody
    public Object load(HttpServletRequest req,
                      @RequestParam(value = "strategy", defaultValue = "merge") String strategy,
                      @RequestParam(value = "file", required = false) MultipartFile springFile) throws IOException {

        log.info("File import started with strategy: {}", strategy);

        // Prefer Spring MultipartFile if available (works with CommonsMultipartResolver)
        String fileName;
        @Cleanup InputStream stream = null;

        if (springFile != null && !springFile.isEmpty()) {
            fileName = springFile.getOriginalFilename();
            if (!StringUtils.endsWithAny(fileName, ".xml", ".zip")) {
                return ImmutableMap.of(STATUS, "not supported");
            }
            if (StringUtils.endsWithAny(fileName, ".zip")) {
                File file = importExportManager.unzip(springFile.getInputStream());
                if (file != null) {
                    stream = new FileInputStream(file);
                } else {
                    return ImmutableMap.of(STATUS, "no file");
                }
            } else {
                stream = springFile.getInputStream();
            }
        } else {
            // Fallback to Servlet Part (works with StandardServletMultipartResolver)
            Part filePart = null;
            try {
                filePart = req.getPart("file");
            } catch (ServletException e) {
                log.debug("Exception while obtaining servlet part: ", e);
            }
            if (filePart == null) {
                return ImmutableMap.of(STATUS, "no file");
            }
            fileName = getFileName(filePart);
            if (!StringUtils.endsWithAny(fileName, ".xml", ".zip")) {
                return ImmutableMap.of(STATUS, "not supported");
            }
            if (StringUtils.endsWithAny(fileName, ".zip")) {
                File file = importExportManager.unzip(filePart.getInputStream());
                if (file != null) {
                    stream = new FileInputStream(file);
                } else {
                    return ImmutableMap.of(STATUS, "no file");
                }
            } else {
                stream = filePart.getInputStream();
            }
        }

        importExportManager.loadMocks(stream, strategy);

        log.info("XML imported!");

        return ImmutableMap.of(STATUS, "ok");
    }

    private String getFileName(Part filePart) {
        String fileName = "";
        Collection<String> headers = filePart.getHeaders("content-disposition");
        if (headers != null) {
            for (String header : headers) {
                if (Pattern.matches(".*filename=\\\".*\\\".*", header)) {
                    fileName = StringUtils.substringBefore(StringUtils.substringAfter(header, "filename=\""), "\"");
                    break;
                }
            }
        }
        return fileName;
    }

}
