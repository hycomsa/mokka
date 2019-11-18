package pl.hycom.mokka.service.payment;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pl.hycom.mokka.service.payment.constant.BlueMediaConstants;
import pl.hycom.mokka.service.payment.pojo.BlueMediaPayment;
import pl.hycom.mokka.util.validation.DefaultHashGenerator;
import pl.hycom.mokka.util.validation.HashAlgorithm;
import pl.hycom.mokka.util.validation.HashGenerator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service implements PaymentStatusService. Used to changing payment status
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@Slf4j
@Service(value = "paymentStatusService")
public class DefaultPaymentStatusService implements PaymentStatusService {

    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    private final RestTemplate restTemplate;

    @Setter
    private HashGenerator hashGenerator;

    @Value("${paymentSchema}")
    private String paymentSchema;


    public DefaultPaymentStatusService(RestTemplateBuilder builder) {
        restTemplate = builder.build();
        hashGenerator = new DefaultHashGenerator();
    }
    /**
     * Generates rest post request with SUCCESS status
     * @param blueMediaPayment BlueMediaPayment model
     */
    @Override
    public void paymentStatusSuccessUpdate(BlueMediaPayment blueMediaPayment) {
        updatePaymentStatus(BlueMediaConstants.STATUS_SUCCESS, blueMediaPayment);
    }
    /**
     * Generates rest post request with PENDING status
     * @param blueMediaPayment BlueMediaPayment model
     */
    @Override
    public void paymentStatusPendingUpdate(BlueMediaPayment blueMediaPayment) {
        updatePaymentStatus(BlueMediaConstants.STATUS_PENDING, blueMediaPayment);
    }
    /**
     * Generates rest post request with FAILURE status
     * @param blueMediaPayment BlueMediaPayment model
     */
    @Override
    public void paymentStatusFailureUpdate(BlueMediaPayment blueMediaPayment) {
        updatePaymentStatus(BlueMediaConstants.STATUS_FAILURE, blueMediaPayment);
    }

    private void updatePaymentStatus(String status, BlueMediaPayment blueMediaPayment) {
        log.info("Update payment with status {}", status);
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
            Template template = cfg.getTemplate(BlueMediaConstants.PAYMENT_STATUS_UPDATE_FTL);
            Writer writer = new OutputStreamWriter(System.out);
            Map<String, Object> dataModel = getDataModel(blueMediaPayment, status);
            template.process(dataModel, writer);

            log.info("Request :[{}]", writer.toString());
            byte[] bytesEncoded = Base64.encodeBase64(writer.toString()
                                                              .getBytes());
            blueMediaPayment.setHash(calculateRedirectHash(blueMediaPayment));
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            String base64String=new String(bytesEncoded);
            log.info("Request transformed to Base64:["+base64String+"]");
            params.add(BlueMediaConstants.TRANSACTIONS, base64String);
            ResponseEntity<String> response = restTemplate.postForEntity(blueMediaPayment.getNotificationURL() , params, String.class);
            log.info("Response status: [{}]", response.getStatusCode());
            log.info("Response body: [{}]", response.getBody());
        } catch (IOException | TemplateException e) {
            log.error("Error occured when generating payment request", e);
        }
    }

    private String generateRandomString(int length) {
        char[] alphNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(alphNum[rnd.nextInt(alphNum.length)]);
        }
        return sb.toString();
    }

    private String calculateUpdateHash(BlueMediaPayment blueMediaPayment, String remoteID, String paymentDate, String status) {
        StringBuilder sb = new StringBuilder(blueMediaPayment.getServiceID());
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(blueMediaPayment.getOrderID());
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(remoteID);
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(blueMediaPayment.getAmount());
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(BlueMediaConstants.CURRENCY);
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(BlueMediaConstants.GATEWAY_ID);
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(paymentDate);
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(status);
        sb.append(BlueMediaConstants.SEPARATOR);
        sb.append(BlueMediaConstants.PAYMENT_STATUS_DETAILS);

        return hashGenerator.generateHash(sb.toString(), HashAlgorithm.SHA256, blueMediaPayment.getKey());
    }

    private String calculateRedirectHash(BlueMediaPayment blueMediaPayment) {
        StringBuilder hashChain = new StringBuilder();
        hashChain.append(blueMediaPayment.getServiceID())
                .append(BlueMediaConstants.SEPARATOR)
                .append(blueMediaPayment.getOrderID());

        return hashGenerator.generateHash(hashChain.toString(), HashAlgorithm.SHA256, blueMediaPayment.getKey());
    }


    private Map<String, Object> getDataModel(BlueMediaPayment blueMediaPayment, String status) {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put(BlueMediaConstants.ORDER_ID_LABEL, blueMediaPayment.getOrderID());
        dataModel.put(BlueMediaConstants.AMOUNT_LABEL, blueMediaPayment.getAmount());
        dataModel.put(BlueMediaConstants.PAYMENT_SCHEMA, paymentSchema);
        dataModel.put(BlueMediaConstants.PAYMENT_STATUS_LABEL, status);
        String date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        dataModel.put(BlueMediaConstants.PAYMENT_DATE_LABEL, date);
        dataModel.put(BlueMediaConstants.CURRENCY_LABEL, BlueMediaConstants.CURRENCY);
        String remoteID = generateRandomString(20);
        dataModel.put(BlueMediaConstants.REMOTE_ID_LABEL, remoteID);
        dataModel.put(BlueMediaConstants.PAYMENT_STATUS_DETAILS_LABEL, BlueMediaConstants.PAYMENT_STATUS_DETAILS);
        dataModel.put(BlueMediaConstants.GATEWAY_ID_LABEL, BlueMediaConstants.GATEWAY_ID);
        dataModel.put(BlueMediaConstants.SERVICE_ID_LABEL, blueMediaPayment.getServiceID());
        dataModel.put(BlueMediaConstants.HASH_LABEL, calculateUpdateHash(blueMediaPayment, remoteID, date, status));
        return dataModel;
    }



}
