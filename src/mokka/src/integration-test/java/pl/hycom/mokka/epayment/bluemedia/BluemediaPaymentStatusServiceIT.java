package pl.hycom.mokka.epayment.bluemedia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@RestClientTest(BluemediaPaymentStatusService.class)
@ActiveProfiles("test")
public class BluemediaPaymentStatusServiceIT {

    @Autowired
    private BluemediaPaymentStatusService bluemediaPaymentStatusService;

    @Autowired
    private MockRestServiceServer server;

    @BeforeEach
    public void init() {
        this.server.expect(requestTo("http://localhost:48080/soap/webservice-http/payment"))
            .andRespond(withSuccess("OK", MediaType.TEXT_PLAIN));
    }

    @Test
    public void shouldSendSuccess() {
        bluemediaPaymentStatusService.paymentStatusSuccessUpdate(createSampleBlueMediaPayment());

        server.verify();
    }

    @Test
    public void shouldSendPending() {
        bluemediaPaymentStatusService.paymentStatusPendingUpdate(createSampleBlueMediaPayment());

        server.verify();
    }

    @Test
    public void shouldSendFailure() {
        bluemediaPaymentStatusService.paymentStatusFailureUpdate(createSampleBlueMediaPayment());

        server.verify();
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
