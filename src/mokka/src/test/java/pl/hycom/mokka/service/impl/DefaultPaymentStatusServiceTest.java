package pl.hycom.mokka.service.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import pl.hycom.mokka.AbstractTest;
import pl.hycom.mokka.service.payment.pojo.BlueMediaPayment;
import pl.hycom.mokka.service.payment.PaymentStatusService;

import javax.annotation.Resource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@Ignore
public class DefaultPaymentStatusServiceTest extends AbstractTest {

    @Resource
    private PaymentStatusService paymentStatusService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(48080);

    @Before
    public void init() {
        wireMockRule.stubFor(post(urlPathMatching("/soap/webservice-http/payment")).willReturn(aResponse().withHeader("Content-Type", "text/plain")
                                                                                                       .withBody("OK")
                                                                                                       .withStatus(200)));
    }

    @Test
    public void TestSuccess() {

        paymentStatusService.paymentStatusSuccessUpdate(createSampleBlueMediaPayment());
    }

    @Test
    public void TestPending() {
        paymentStatusService.paymentStatusPendingUpdate(createSampleBlueMediaPayment());
    }

    @Test
    public void TestFailure() {
        paymentStatusService.paymentStatusFailureUpdate(createSampleBlueMediaPayment());
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
