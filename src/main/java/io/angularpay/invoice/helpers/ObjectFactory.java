package io.angularpay.invoice.helpers;

import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.RequestStatus;

import java.util.UUID;

import static io.angularpay.invoice.common.Constants.SERVICE_CODE;
import static io.angularpay.invoice.util.SequenceGenerator.generateRequestTag;

public class ObjectFactory {

    public static InvoiceRequest pmtRequestWithDefaults() {
        return InvoiceRequest.builder()
                .reference(UUID.randomUUID().toString())
                .serviceCode(SERVICE_CODE)
                .status(RequestStatus.ACTIVE)
                .requestTag(generateRequestTag())
                .build();
    }
}