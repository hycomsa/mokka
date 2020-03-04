package pl.hycom.mokka.epayment.bluemedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@Data
class BlueMediaPayment {

    @JsonProperty("ServiceID")
    private String serviceID;

    @JsonProperty("OrderID")
    private String orderID;

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("CustomerEmail")
    private String customerEmail;

    @JsonProperty("Hash")
    private String hash;

    @JsonProperty("RedirectURL")
    private String redirectURL;

    @JsonProperty("NotificationURL")
    private String notificationURL;

    @JsonProperty("Key")
    private String key;

}
