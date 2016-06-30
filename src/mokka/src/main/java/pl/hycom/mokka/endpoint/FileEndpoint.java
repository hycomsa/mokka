package pl.hycom.mokka.endpoint;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.hycom.mokka.service.file.FileService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@RequestMapping(value = "/files")
@RestController
public class FileEndpoint {
    private static final Logger LOG = Logger.getLogger(FileEndpoint.class);
    private static final int BUFFER_SIZE = 4096;
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/{file-id:.+}",
                    method = RequestMethod.GET,
                    produces = "application/octet-stream")
    public void fetchFile(
            @PathVariable("file-id")
            String fileId,
            @RequestParam(name = "ext",required = false)
            String extension, HttpServletResponse response, HttpServletRequest request) throws IOException {
        LOG.debug("Invoking FileEndpoint#fetchFile with file-id [" + fileId + "]");
        ServletContext context = request.getServletContext();
        String fileName = fileId;
        if (StringUtils.isNotBlank(extension)) {
            fileName = fileId + "." + extension;
        }
        File file = fileService.fetchFile(fileName);
        if (file == null) {
            LOG.warn("Could not find file with given name [" + fileName + "]");
            return;
        }

        String mimeType = context.getMimeType(file.getAbsolutePath());
        response.setContentType(mimeType);

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
        response.setHeader(headerKey, headerValue);
        response.setContentLength((int) file.length());

        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        FileInputStream inputStream = new FileInputStream(file);
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
        LOG.debug("Ending FileEndpoint#fetchFile");
    }

}
