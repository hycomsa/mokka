package pl.hycom.mokka.epayment.bluemedia;

/**
 * Payment Status interface used to changing payment status
 * @author Mariusz Krysztofowicz (mariusz.krysztofowicz@hycom.pl)
 */
public interface PaymentStatusService {
    /**
     * Updates payment status with SUCCESS value
     * @param blueMediaPayment BlueMediaPayment model
     */
    void paymentStatusSuccessUpdate(BlueMediaPayment blueMediaPayment);
    /**
     * Updates payment status with PENDING value
     * @param blueMediaPayment BlueMediaPayment model
     */
    void paymentStatusPendingUpdate(BlueMediaPayment blueMediaPayment);
    /**
     * Updates payment status with FAILURE value
     * @param blueMediaPayment BlueMediaPayment model
     */
    void paymentStatusFailureUpdate(BlueMediaPayment blueMediaPayment);
}
