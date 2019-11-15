package pl.hycom.mokka.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import pl.hycom.mokka.service.payment.DefaultPaymentStatusService;
import pl.hycom.mokka.service.payment.pojo.BlueMediaPayment;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@Disabled
public class DefaultPaymentStatusServiceTest{

    private DefaultPaymentStatusService defaultPaymentStatusService;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    public void init() {
        defaultPaymentStatusService = new DefaultPaymentStatusService();//new DefaultHashGenerator());
        this.server.expect(requestTo("/soap/webservice-http/payment"))
            .andRespond(withSuccess("OK", MediaType.TEXT_PLAIN));
    }

    @Test
    public void shouldSendSuccess() {
        defaultPaymentStatusService.paymentStatusSuccessUpdate(createSampleBlueMediaPayment());
    }

    @Test
    public void shouldSendPending() {
        defaultPaymentStatusService.paymentStatusPendingUpdate(createSampleBlueMediaPayment());
    }

    @Test
    public void shouldSendFailure() {
        defaultPaymentStatusService.paymentStatusFailureUpdate(createSampleBlueMediaPayment());
    }

    private BlueMediaPayment createSampleBlueMediaPayment() {
        BlueMediaPayment blueMediaPayment = new BlueMediaPayment();
        blueMediaPayment.setAmount("123.12");
        blueMediaPayment.setServiceID("6123333");
        blueMediaPayment.setOrderID("123456789");
        blueMediaPayment.setKey("key");
        blueMediaPayment.setCustomerEmail("jan@kowalski.com");
        blueMediaPayment.setNotificationURL("http://localhost:48080/soap/webservice-http/payment");
        blueMediaPayment.setRedirectURL("http://hycom.pl");
        return blueMediaPayment;
    }

}
