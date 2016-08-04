package pl.hycom.mokka.ui.payment;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;
import pl.hycom.mokka.service.payment.PaymentStatusService;
import pl.hycom.mokka.service.payment.pojo.BlueMediaPayment;
import pl.hycom.mokka.util.validation.HashValidator;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * Controller for BlueMedia payment
 *
 * @author Jakub Muras <jakub.muras@hycom.pl>
 */
@Controller
@RequestMapping(value = "/bluemedia")
public class PaymentController {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String SERVICE_ID_EMPTY = "ServiceID cannot be empty.";
    public static final String SERVICE_ID_TOO_LONG_MAXIMUM_10_SIGNS = "ServiceID too long. Maximum 10 signs.";
    public static final String ORDER_ID_CANNOT_BE_EMPTY = "OrderID cannot be empty.";
    public static final String ORDER_ID_TOO_LONG_MAXIMUM_32_SIGNS = "OrderID too long. Maximum 32 signs.";
    public static final String WRONG_EMAIL_FORMAT = "Wrong email format.";
    public static final String AMOUNT_COULDNT_BE_NULL = "Amount couldn't be null";
    public static final String HASH_COULDNT_BE_NULL = "Hash couldn't be null";
    public static final String AMOUNT_MUST_NOT_BE_0 = "Amount must be >= 0";
    public static final String WRONG_HASH_VALUE = "Wrong hash value";
    public static final String ONLY_INTEGERS_ARE_ALLOWED = "Only integers are allowed";
    public static final String ONLY_ALFANUMERIC_SIGNS_ARE_ALLOWED = "Only alfanumeric signs are allowed";
    public static final String ALFANUMERIC_REGEX = "^[a-zA-Z0-9]*$";
    public static final String INT_REGEX = "^[0-9]*$";
    public static final String AMOUNT_FORMAT = "Amount should have max 14 digits before dot and 2 digits after dot.";
    public static final String AMOUNT_REGEX = "^[\\d]{0,14}(\\.\\d{2})?$";
    private static final String REDIRECT = "redirect:";
    private static final String ORDER_ID = "&OrderID=";
    private static final String SERVICE_ID = "?ServiceID=";
    private static final String HASH = "&Hash=";
    public static final String ORDER_IDERROR = "OrderIDerror";
    public static final String AMOUNTERROR = "Amounterror";
    public static final String SERVICE_IDERROR = "ServiceIDerror";
    @Resource
    private PaymentStatusService paymentStatusService;


    @Value("${privateKey}")
    private String privateKey;
    @Value("${redirectURL}")
    private String redirectURL;
    @Value("${notificationURL}")
    private String notificationURL;

    @RequestMapping(method = RequestMethod.GET)
    public String get(
            @RequestParam("ServiceID")
            String serviceId,
            @RequestParam("OrderID")
            String orderId,
            @RequestParam("Amount")
            String amount,
            @RequestParam(value = "Key",
                          required = false)
            String key,
            @RequestParam(name = "CustomerEmail",
                          required = false)
            String customerEmail,
            @RequestParam("Hash")
            String hash, Model model) {
        String currentKey = StringUtils.isEmptyOrWhitespace(key) ? privateKey : key;
        if (StringUtils.isEmptyOrWhitespace(serviceId)) {
            model.addAttribute(SERVICE_IDERROR, SERVICE_ID_EMPTY);
        } else if (serviceId.length() > 10) {
            model.addAttribute(SERVICE_IDERROR, SERVICE_ID_TOO_LONG_MAXIMUM_10_SIGNS);
        } else if (!Pattern.compile(INT_REGEX)
                .matcher(serviceId)
                .matches()) {
            model.addAttribute(SERVICE_IDERROR, ONLY_INTEGERS_ARE_ALLOWED);
        }
        addOrderIdToModel(orderId, model);
        addAmountToModel(amount, model);
        if (!StringUtils.isEmptyOrWhitespace(customerEmail) && !Pattern.compile(EMAIL_PATTERN)
                .matcher(customerEmail)
                .matches()) {
            model.addAttribute("Emailerror", WRONG_EMAIL_FORMAT);
        }
        if (StringUtils.isEmptyOrWhitespace(hash)) {
            model.addAttribute("Hasherror", HASH_COULDNT_BE_NULL);
        } else if (!HashValidator.validate(hash, currentKey, serviceId, orderId, amount, customerEmail)) {
            model.addAttribute("Hasherror", WRONG_HASH_VALUE);
        }

        BlueMediaPayment blueMediaPayment = new BlueMediaPayment();
        blueMediaPayment.setOrderID(orderId);
        blueMediaPayment.setServiceID(serviceId);
        blueMediaPayment.setAmount(amount);
        blueMediaPayment.setCustomerEmail(customerEmail);
        blueMediaPayment.setNotificationURL(notificationURL);
        blueMediaPayment.setRedirectURL(redirectURL);
        blueMediaPayment.setKey(currentKey);
        blueMediaPayment.setHash(hash);
        model.addAttribute("blueMediaPayment", blueMediaPayment);

        return "bluemedia";
    }

    private void addOrderIdToModel(String orderId, Model model) {
        if (StringUtils.isEmptyOrWhitespace(orderId)) {
            model.addAttribute(ORDER_IDERROR, ORDER_ID_CANNOT_BE_EMPTY);
        } else if (orderId.length() > 32) {
            model.addAttribute(ORDER_IDERROR, ORDER_ID_TOO_LONG_MAXIMUM_32_SIGNS);
        } else if (!Pattern.compile(ALFANUMERIC_REGEX)
                .matcher(orderId)
                .matches()) {
            model.addAttribute(ORDER_IDERROR, ONLY_ALFANUMERIC_SIGNS_ARE_ALLOWED);
        }
    }

    private void addAmountToModel(String amount, Model model) {
        try {
            if (StringUtils.isEmptyOrWhitespace(amount)) {
                model.addAttribute(AMOUNTERROR, AMOUNT_COULDNT_BE_NULL);
            } else if (Double.parseDouble(amount) < 0) {
                model.addAttribute(AMOUNTERROR, AMOUNT_MUST_NOT_BE_0);
            } else if (!Pattern.compile(AMOUNT_REGEX)
                    .matcher(amount)
                    .matches()) {
                model.addAttribute(AMOUNTERROR, AMOUNT_FORMAT);
            }
        } catch (NumberFormatException e) {
            model.addAttribute(AMOUNTERROR, "Wrong number format");
        }
    }


    @RequestMapping(method = RequestMethod.POST,
                    value = "/pay")
    public String successRedirect(
            @ModelAttribute
            BlueMediaPayment blueMediaPayment) {
        if (!StringUtils.isEmpty(blueMediaPayment.getNotificationURL())) {
            paymentStatusService.paymentStatusSuccessUpdate(blueMediaPayment);
        }
        return REDIRECT + redirectUrl(blueMediaPayment) + HASH + blueMediaPayment.getHash();
    }


    @RequestMapping(method = RequestMethod.POST,
                    value = "/pending")
    @ResponseBody
    public String pendingRedirect(
            @ModelAttribute
            BlueMediaPayment blueMediaPayment) {

        paymentStatusService.paymentStatusPendingUpdate(blueMediaPayment);
        return "OK";
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = "/error")
    public String errorRedirect(
            @ModelAttribute
            BlueMediaPayment blueMediaPayment) {
        if (!StringUtils.isEmpty(blueMediaPayment.getNotificationURL())) {
            paymentStatusService.paymentStatusFailureUpdate(blueMediaPayment);
        }
        return REDIRECT + redirectUrl(blueMediaPayment) + HASH + blueMediaPayment.getHash();
    }

    private String redirectUrl(BlueMediaPayment blueMediaPayment) {

        return blueMediaPayment.getRedirectURL() + SERVICE_ID + blueMediaPayment.getServiceID() + ORDER_ID + blueMediaPayment.getOrderID();


    }

}
