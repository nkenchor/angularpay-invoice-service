
package io.angularpay.invoice.models;

import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.commands.InvoiceRequestSupplier;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class GenericCommandResponse extends GenericReferenceResponse implements InvoiceRequestSupplier {

    private final String requestReference;
    private final String itemReference;
    private final InvoiceRequest invoiceRequest;
}
