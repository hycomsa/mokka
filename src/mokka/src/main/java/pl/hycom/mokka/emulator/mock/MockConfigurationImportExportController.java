package pl.hycom.mokka.emulator.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;

import lombok.Cleanup;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Controller
@Slf4j
public class MockConfigurationImportExportController {

	@Autowired
	private pl.hycom.mokka.emulator.mock.MockConfigurationImportExportManager importExportManager;

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EDITOR')")
	@RequestMapping(value = "/configurations/export")
	public Object export(HttpServletResponse res) {

		byte[] documentBody = new byte[0];
		try {
			File csvFile = importExportManager.createExportFile();
			if (csvFile != null) {
				File zipFile = importExportManager.zipFile(csvFile);
				if (zipFile != null) {
					documentBody = Files.readAllBytes(zipFile.toPath());
				}
			}
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			log.error("", e);
		}

		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "zip"));
		header.setContentDispositionFormData("attachment", "mock-configuration-export-" + DateFormatUtils.format(new Date(), "yyyy-MM-dd-HH-mm-ss-SSS") + ".zip");
		header.setContentLength(documentBody.length);

		return new HttpEntity<byte[]>(documentBody, header);
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
	@RequestMapping(value = "/configurations/import", method = RequestMethod.POST)
	@ResponseBody
	public Object load(HttpServletRequest req) throws Exception {

		log.info("CSV import started");

		final Part filePart = req.getPart("file");
		if (filePart == null) {
			return ImmutableMap.of("status", "no file");
		}

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

		if (!StringUtils.endsWithAny(fileName, ".xml", ".zip")) {
			return ImmutableMap.of("status", "not supported");
		}

		@Cleanup
		InputStream stream = null;
		File file = null;
		if (StringUtils.endsWithAny(fileName, ".zip")) {
			file = importExportManager.unzip(filePart.getInputStream());
			if (file != null) {
				stream = new FileInputStream(file);
			} else {
				return ImmutableMap.of("status", "no file");
			}
		} else {
			stream = filePart.getInputStream();
		}

		importExportManager.loadMocks(stream);

		log.info("XML imported!");

		return ImmutableMap.of("status", "ok");
	}

}