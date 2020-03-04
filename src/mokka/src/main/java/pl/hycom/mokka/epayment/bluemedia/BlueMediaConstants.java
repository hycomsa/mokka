package pl.hycom.mokka.epayment.bluemedia;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * BlueMedia Constants interface
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class BlueMediaConstants {
    static final String SEPARATOR = "|";
    static final String GATEWAY_ID = "mokka";
    static final String CURRENCY = "PLN";
    static final String PAYMENT_STATUS_DETAILS = "AUTHORIZED";
    static final String STATUS_SUCCESS = "SUCCESS";
    static final String STATUS_PENDING = "PENDING";
    static final String STATUS_FAILURE = "FAILURE";
    static final String ORDER_ID_LABEL = "orderID";
    static final String AMOUNT_LABEL = "amount";
    static final String PAYMENT_STATUS_LABEL = "paymentStatus";
    static final String PAYMENT_DATE_LABEL = "paymentDate";
    static final String CURRENCY_LABEL = "currency";
    static final String REMOTE_ID_LABEL = "remoteID";
    static final String PAYMENT_STATUS_DETAILS_LABEL = "paymentStatusDetails";
    static final String GATEWAY_ID_LABEL = "gatewayID";
    static final String SERVICE_ID_LABEL = "serviceID";
    static final String HASH_LABEL = "hash";
    static final String TRANSACTIONS = "transactions";
    static final String PAYMENT_STATUS_UPDATE_FTL = "paymentStatusUpdate.ftl";
    static final String PAYMENT_SCHEMA = "paymentSchema";
}
