package io.angularpay.invoice.domain.commands;

import io.angularpay.invoice.domain.InvoiceRequest;

public interface InvoiceRequestSupplier {
    InvoiceRequest getInvoiceRequest();
}
