package pl.hycom.mokka.service.payment.pojo;

/**
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public class BlueMediaPayment {
    private String ServiceID;
    private String OrderID;
    private String Amount;
    private String CustomerEmail;
    private String Hash;
    private String RedirectURL;
    private String NotificationURL;
    private String Key;

    public String getServiceID() {
        return ServiceID;
    }

    public void setServiceID(String serviceID) {
        ServiceID = serviceID;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        CustomerEmail = customerEmail;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public String getRedirectURL() {
        return RedirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        RedirectURL = redirectURL;
    }

    public String getNotificationURL() {
        return NotificationURL;
    }

    public void setNotificationURL(String notificationURL) {
        NotificationURL = notificationURL;
    }
    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        this.Key = key;
    }
}
