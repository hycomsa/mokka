package pl.hycom.mokka.endpoint;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.hycom.mokka.service.file.FileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLConnection;
import java.util.Objects;

/**
 * Rest controller responsible for file management
 *
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@RequestMapping(value = "/files")
@RestController
public class FileEndpoint {
    private static final Logger LOG = Logger.getLogger(FileEndpoint.class);
    /**
     * FileService
     */
    @Autowired
    private FileService fileService;

    /**
     * Method returns file  with given file name wrapped with ResponseEntity,
     * if extension argument is empty returns HttpStatus.BAD_REQUEST,
     * if file doesn't exist returns HttpStatus.NOT_FOUND
     *
     * @param fileId
     *         File name path variable
     * @param extension
     *         File extension query argument
     * @return ResponseEntity<FileSystemResource>
     */
    @RequestMapping(value = "/{file-id}",
                    method = RequestMethod.GET,
                    produces = "application/octet-stream")
    @ResponseBody
    public ResponseEntity<FileSystemResource> fetchFile(
            @PathVariable("file-id")
            String fileId,
            @RequestParam(name = "ext")
            String extension) {

        String fileName = null;
        File file;
        try {
            Objects.requireNonNull(extension);
            fileName = fileId + "." + extension;
            file = fileService.fetchFile(fileName);
        } catch (FileNotFoundException e) {
            LOG.warn("Could not find file with given name [" + fileName + "]", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NullPointerException e) {
            LOG.warn("Extension cannot be null", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (StringUtils.isNotBlank(mimeType) && MediaType.parseMediaType(mimeType) != null) {
            headers.setContentType(MediaType.parseMediaType(mimeType));
        }
        headers.setContentLength(file.length());
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        LOG.debug("Ending FileEndpoint#fetchFile with status [" + HttpStatus.OK + "]");
        return new ResponseEntity<>(fileSystemResource, HttpStatus.OK);
    }


}


