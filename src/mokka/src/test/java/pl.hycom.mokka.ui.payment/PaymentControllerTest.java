package pl.hycom.mokka.ui.payment;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import pl.hycom.mokka.AbstractTest;
import pl.hycom.mokka.service.payment.pojo.BlueMediaPayment;

import javax.annotation.Resource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
@Ignore
public class PaymentControllerTest extends AbstractTest{
    private static final String REDIRECT = "redirect:";
    private static final String ORDER_ID = "&OrderID=";
    private static final String HASH = "&Hash=";
    private static final String SERVICE_ID = "?ServiceID=";
    @Resource
    private PaymentController paymentController;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(48080);

    @Before
    public void init() {
        wireMockRule.stubFor(post(urlPathMatching("/soap/webservice-http/payment")).willReturn(aResponse().withHeader("Content-Type", "text/plain")
                                                                                                       .withBody("OK")
                                                                                                       .withStatus(200)));
    }
    @Test
    public void testGet(){
        Model model = new ExtendedModelMap();
        paymentController.get(" ", " ", "0.0","", "a", "asd", model);
        Assert.assertNotNull(model.asMap().get("ServiceIDerror"));
        Assert.assertEquals(PaymentController.SERVICE_ID_EMPTY, model.asMap().get("ServiceIDerror"));
        Assert.assertNotNull(model.asMap().get("OrderIDerror"));
        Assert.assertEquals(PaymentController.ORDER_ID_CANNOT_BE_EMPTY, model.asMap().get("OrderIDerror"));
        Assert.assertNotNull(model.asMap().get("Emailerror"));
        model = new ExtendedModelMap();
        paymentController.get("12312312311", "123123123111231231231112312312311", "-10.0","", "a" ,"asd", model);
        Assert.assertEquals(PaymentController.SERVICE_ID_TOO_LONG_MAXIMUM_10_SIGNS, model.asMap().get("ServiceIDerror"));
        Assert.assertNotNull(model.asMap().get("OrderIDerror"));
        Assert.assertEquals(PaymentController.ORDER_ID_TOO_LONG_MAXIMUM_32_SIGNS, model.asMap().get("OrderIDerror"));
        paymentController.get("1231s2311", "1231231+=1231231231112312312311", "-10.0", "", "a","", model);
        Assert.assertEquals(PaymentController.ONLY_INTEGERS_ARE_ALLOWED, model.asMap().get("ServiceIDerror"));
        Assert.assertEquals(PaymentController.ONLY_ALFANUMERIC_SIGNS_ARE_ALLOWED, model.asMap().get("OrderIDerror"));
        Assert.assertEquals(PaymentController.HASH_COULDNT_BE_NULL, model.asMap().get("Hasherror"));
        model = new ExtendedModelMap();
        paymentController.get("123123111", "1231231231112231231112312312311", "-10.0","", "as@as.as", "asd", model);
        Assert.assertNull(model.asMap().get("ServiceIDerror"));
        Assert.assertNull(model.asMap().get("OrderIDerror"));
        Assert.assertNull(model.asMap().get("Emailerror"));
        paymentController.get(null, null, "0.0", "","a", null, model);
        Assert.assertNotNull(model.asMap().get("ServiceIDerror"));
        Assert.assertEquals(PaymentController.SERVICE_ID_EMPTY, model.asMap().get("ServiceIDerror"));
        Assert.assertNotNull(model.asMap().get("OrderIDerror"));
        Assert.assertEquals(PaymentController.ORDER_ID_CANNOT_BE_EMPTY, model.asMap().get("OrderIDerror"));
        Assert.assertEquals(PaymentController.HASH_COULDNT_BE_NULL, model.asMap().get("Hasherror"));
        Assert.assertNotNull(model.asMap().get("Emailerror"));
    }

    @Test
    public void testSuccessRedirect() {
        Model model = new ExtendedModelMap();
        BlueMediaPayment blueMediaPayment =  createSampleBlueMediaPayment();
        String result=paymentController.successRedirect(blueMediaPayment);
        Assert.assertEquals(redirectUrl(blueMediaPayment),result);
    }

    @Test
    public void testPendingRedirect() {
        Model model = new ExtendedModelMap();
        BlueMediaPayment blueMediaPayment =  createSampleBlueMediaPayment();
        String result=paymentController.pendingRedirect(createSampleBlueMediaPayment());
        Assert.assertEquals("OK",result);
    }

    @Test
    public void testErrorRedirect() {
        Model model = new ExtendedModelMap();
        BlueMediaPayment blueMediaPayment =  createSampleBlueMediaPayment();
        String result=paymentController.errorRedirect(blueMediaPayment);
        Assert.assertEquals(redirectUrl(blueMediaPayment),result);
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
    private String redirectUrl(BlueMediaPayment blueMediaPayment) {
        return REDIRECT+blueMediaPayment.getRedirectURL() + SERVICE_ID + blueMediaPayment.getServiceID() + ORDER_ID + blueMediaPayment.getOrderID()+HASH+blueMediaPayment.getHash();
    }
}
