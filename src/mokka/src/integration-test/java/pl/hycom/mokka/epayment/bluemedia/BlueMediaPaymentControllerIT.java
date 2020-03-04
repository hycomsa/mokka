package pl.hycom.mokka.epayment.bluemedia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.hycom.mokka.emulator.mock.MockInterceptor;
import pl.hycom.mokka.security.ChangePasswordInterceptor;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Jakub Muras <jakub.muras@hycom.pl>
 * @author Piotr Kulasek-Szwed (kulasekp@gmail.com)
 */
@WebMvcTest(BlueMediaPaymentController.class)
@ActiveProfiles("test")
@Import(BlueMediaConfiguration.class)
public class BlueMediaPaymentControllerIT {

    private static final String ORDER_ID = "&OrderID=";

    private static final String HASH = "&Hash=";

    private static final String SERVICE_ID = "?ServiceID=";

    @MockBean
    ChangePasswordInterceptor changePasswordInterceptor;

    @MockBean
    MockInterceptor mockInterceptor;

    @MockBean
    PaymentStatusService paymentStatusService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void init() throws Exception {
        when(mockInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Boolean.TRUE);
        when(changePasswordInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Boolean.TRUE);
        doNothing().when(paymentStatusService).paymentStatusFailureUpdate(Mockito.any());
        doNothing().when(paymentStatusService).paymentStatusPendingUpdate(Mockito.any());
        doNothing().when(paymentStatusService).paymentStatusSuccessUpdate(Mockito.any());
    }

    @Test
    public void shouldFailIfServiceIdEmpty() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "").
                param("OrderID", "").
                param("Amount", "0").
                param("Hash", "")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.SERVICE_IDERROR, BlueMediaPaymentController.SERVICE_ID_EMPTY));
    }

    @Test
    public void shouldFailIfServiceIdTooLong() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "01234567891011").
                param("OrderID", "").
                param("Amount", "0").
                param("Hash", "")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.SERVICE_IDERROR,
                                        BlueMediaPaymentController.SERVICE_ID_TOO_LONG_MAXIMUM_10_SIGNS));
    }

    @Test
    public void shouldFailIfServiceIdNotInteger() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "2341+---").
                param("OrderID", "").
                param("Amount", "0").
                param("Hash", "")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.SERVICE_IDERROR,
                                        BlueMediaPaymentController.ONLY_INTEGERS_ARE_ALLOWED));
    }

    @Test
    public void shouldFailIfOrderIdEmpty() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "1").
                param("OrderID", "").
                param("Amount", "0").
                param("Hash", "")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.ORDER_IDERROR, BlueMediaPaymentController.ORDER_ID_CANNOT_BE_EMPTY));
    }

    @Test
    public void shouldFailIfOrderIdTooLong() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "1").
                param("OrderID", "0123456789101112131415161718192021222324252627282930").
                param("Amount", "0").
                param("Hash", "")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.ORDER_IDERROR,
                                        BlueMediaPaymentController.ORDER_ID_TOO_LONG_MAXIMUM_32_SIGNS));
    }

    @Test
    public void shouldFailIfOrderIdNonAlphanumeric() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "1").
                param("OrderID", "01234$%!!4151627282930").
                param("Amount", "0").
                param("Hash", "")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.ORDER_IDERROR,
                                        BlueMediaPaymentController.ONLY_ALFANUMERIC_SIGNS_ARE_ALLOWED));
    }

    @Test
    public void shouldFailIfHashEmpty() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "1").
                param("OrderID", "1").
                param("Amount", "0").
                param("Hash", "")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.HASH_ERROR, BlueMediaPaymentController.HASH_COULDNT_BE_NULL));
    }

    @Test
    public void shouldFailIfWrongHash() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "1").
                param("OrderID", "1").
                param("Amount", "0").
                param("Hash", "asda")).
            andExpect(status().isOk()).
            andExpect(model().attribute(BlueMediaPaymentController.HASH_ERROR, BlueMediaPaymentController.WRONG_HASH_VALUE));
    }

    @Test
    public void shouldGetSuccessfully() throws Exception {
        this.mvc.perform(get("/bluemedia").accept(MediaType.TEXT_PLAIN).
                param("ServiceID", "123123111").
                param("OrderID", "11").
                param("Amount", "0.00").
                param("CustomerEmail", "test@test.pl").
                param("Hash", "b05bc1f89b61d68b57eacf83d28f79b6f9dc7e9b20e77b0d3f676475721f7")).
            andExpect(status().isOk()).
            andExpect(model().attributeDoesNotExist(BlueMediaPaymentController.SERVICE_IDERROR)).
            andExpect(model().attributeDoesNotExist(BlueMediaPaymentController.ORDER_IDERROR)).
            andExpect(model().attributeDoesNotExist(BlueMediaPaymentController.HASH_ERROR));
    }

    @Test
    public void shouldSuccessRedirect() throws Exception {
        BlueMediaPayment blueMediaPayment = createSampleBlueMediaPayment();

        this.mvc.perform(post("/bluemedia/pay").flashAttr("blueMediaPayment", blueMediaPayment)).
            andExpect(status().is3xxRedirection()).
            andExpect(redirectedUrl(redirectUrl(blueMediaPayment)));
    }

    @Test
    public void shouldErrorRedirect() throws Exception {
        BlueMediaPayment blueMediaPayment = createSampleBlueMediaPayment();

        this.mvc.perform(post("/bluemedia/error").flashAttr("blueMediaPayment", blueMediaPayment)).
            andExpect(status().is3xxRedirection()).
            andExpect(redirectedUrl(redirectUrl(blueMediaPayment)));
    }

    @Test
    public void shouldPendingRedirect() throws Exception {
        BlueMediaPayment blueMediaPayment = createSampleBlueMediaPayment();

        this.mvc.perform(post("/bluemedia/pending", blueMediaPayment)).
            andExpect(status().isOk()).
            andExpect(content().string("OK"));

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
        return blueMediaPayment.getRedirectURL() + SERVICE_ID + blueMediaPayment
            .getServiceID() + ORDER_ID + blueMediaPayment.getOrderID() + HASH + blueMediaPayment.getHash();
    }
}
