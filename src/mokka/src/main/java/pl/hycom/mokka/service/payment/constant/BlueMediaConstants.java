package pl.hycom.mokka.service.payment.constant;

/**
 * BlueMedia Constants interface
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public interface BlueMediaConstants {
    String SEPARATOR = "|";
    String GATEWAY_ID = "mokka";
    String CURRENCY = "PLN";
    String PAYMENT_STATUS_DETAILS = "AUTHORIZED";
    String STATUS_SUCCESS = "SUCCESS";
    String STATUS_PENDING = "PENDING";
    String STATUS_FAILURE = "FAILURE";
    String ORDER_ID_LABEL = "orderID";
    String AMOUNT_LABEL = "amount";
    String PAYMENT_STATUS_LABEL = "paymentStatus";
    String PAYMENT_DATE_LABEL = "paymentDate";
    String CURRENCY_LABEL = "currency";
    String REMOTE_ID_LABEL = "remoteID";
    String PAYMENT_STATUS_DETAILS_LABEL = "paymentStatusDetails";
    String GATEWAY_ID_LABEL = "gatewayID";
    String SERVICE_ID_LABEL = "serviceID";
    String HASH_LABEL = "hash";
    String TRANSACTIONS = "transactions";
    String PAYMENT_STATUS_UPDATE_VM = "templates/paymentStatusUpdate.vm";
    String PAYMENT_SCHEMA = "paymentSchema";
}
