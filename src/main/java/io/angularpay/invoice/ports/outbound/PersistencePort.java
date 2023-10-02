package io.angularpay.invoice.ports.outbound;

import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersistencePort {
    InvoiceRequest createRequest(InvoiceRequest request);
    InvoiceRequest updateRequest(InvoiceRequest request);
    Optional<InvoiceRequest> findRequestByReference(String reference);
    Page<InvoiceRequest> listRequests(Pageable pageable);
    Page<InvoiceRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses);
    Page<InvoiceRequest> findByMerchantUserReference(Pageable pageable, String userReference);
    long getCountByRequestStatus(RequestStatus status);
    long getTotalCount();
}
