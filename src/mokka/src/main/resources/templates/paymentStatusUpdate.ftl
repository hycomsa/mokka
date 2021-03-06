<?xml version="1.0" encoding="UTF-8"?>
<transactionList xmlns="${paymentSchema}">
    <serviceID>${serviceID}</serviceID>
    <transactions>
        <transaction>
            <orderID>${orderID}</orderID>
            <remoteID>${remoteID}</remoteID>
            <amount>${amount}</amount>
            <currency>${currency}</currency>
            <gatewayID>${gatewayID}</gatewayID>
            <paymentDate>${paymentDate}</paymentDate>
            <paymentStatus>${paymentStatus}</paymentStatus>
            <paymentStatusDetails>${paymentStatusDetails}</paymentStatusDetails>
        </transaction>
    </transactions>
    <hash>${hash}</hash>
</transactionList>
