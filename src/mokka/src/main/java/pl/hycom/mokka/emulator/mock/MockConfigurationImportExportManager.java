package pl.hycom.mokka.emulator.mock;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.hycom.mokka.emulator.mock.model.GroovyConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;
import pl.hycom.mokka.emulator.mock.model.StringConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.XmlConfigurationContent;
import pl.hycom.mokka.emulator.mock.model.XmlRoot;
import pl.hycom.mokka.util.query.Q;
import pl.hycom.mokka.util.query.QManager;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Hubert Pruszyński <hubert.pruszynski@hycom.pl>, HYCOM S.A.
 */
@Service
@Slf4j
public class MockConfigurationImportExportManager {

    @Autowired
    private QManager qManager;

    @Autowired
    private MockConfigurationManager mockConfigurationManager;

    @Transactional
    public File createExportFile() throws IOException {
        File file = File.createTempFile("mce-", ".xml");
        file.deleteOnExit();

        try (FileWriter out = new FileWriter(file)) {
            JAXBContext jaxbContext = getJaxbContext();
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            XmlRoot root = new XmlRoot();
            root.setMocks(qManager.execute(Q.select("m").from("MockConfig m"), MockConfiguration.class));
            jaxbMarshaller.marshal(root, out);

        } catch (JAXBException e) {
            log.error("", e);
        }

        return file;
    }

    private JAXBContext getJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(XmlRoot.class, MockConfiguration.class, GroovyConfigurationContent.class,
                                       XmlConfigurationContent.class, StringConfigurationContent.class);
    }

    public void loadMocks(InputStream stream) {

        try {
            JAXBContext jaxbContext = getJaxbContext();
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            XmlRoot root = (XmlRoot) jaxbUnmarshaller.unmarshal(stream);

            for (MockConfiguration mc : root.getMocks()) {
                Q q = Q.select("m").from("MockConfig m").where(Q.eq("m.path", mc.getPath()));
                if (StringUtils.isNotBlank(mc.getPattern())) {
                    q.and(Q.eq("m.pattern", mc.getPattern()));
                } else {
                    q.and(Q.isNull("m.pattern"));
                }

                List<MockConfiguration> databaseMocks = qManager.execute(q, MockConfiguration.class);
                if (databaseMocks.isEmpty() && mc.getConfigurationContent() != null) {
                    mc.setId(null);
                    mc.setDescription("[FILE UPLOAD] " + mc.getDescription());
                    mc.getConfigurationContent().setId(null);
                    mockConfigurationManager.saveOrUpdateMockConfiguration(mc);

                } else if (databaseMocks.size() == 1) {
                    mc.setId(databaseMocks.get(0).getId());
                    mockConfigurationManager.saveOrUpdateMockConfiguration(mc);

                } else {
                    mc.setId(null);
                    mc.setEnabled(false);
                    mc.setDescription("[FILE UPLOAD] " + mc.getDescription());
                    mockConfigurationManager.saveOrUpdateMockConfiguration(mc);
                }
            }

        } catch (JAXBException e) {
            log.error("", e);
        }
    }

    public File zipFile(File file) throws IOException {
        File zipFile = new File(StringUtils.removeEnd(System.getProperty("java.io.tmpdir"),
                                                      File.separator) + File.separator + "mock-configuration-export" + ".zip");
        zipFile.deleteOnExit();

        @Cleanup
        FileOutputStream fos = new FileOutputStream(zipFile, false);

        try (ZipOutputStream zos = new ZipOutputStream(fos)) {
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);

            try (FileInputStream in = new FileInputStream(file)) {
                IOUtils.copy(in, zos);
            }

            zos.closeEntry();
        }


        return zipFile;
    }

    public File unzip(InputStream stream) throws IOException {
        File file = null;

        @Cleanup
        ZipInputStream zis = new ZipInputStream(stream);
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {

            if (!StringUtils.endsWith(ze.getName(), ".xml")) {
                ze = zis.getNextEntry();
            } else {
                file = Files.createTempFile("mce-", "-" + ze.getName()).toFile();
                file.deleteOnExit();

                @Cleanup
                FileOutputStream fos = new FileOutputStream(file);

                IOUtils.copy(zis, fos);
                break;
            }
        }

        zis.closeEntry();

        return file;
    }

}
