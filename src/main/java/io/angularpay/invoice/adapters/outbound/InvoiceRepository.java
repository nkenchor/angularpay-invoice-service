package io.angularpay.invoice.adapters.outbound;

import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<InvoiceRequest, String> {

    Optional<InvoiceRequest> findByReference(String reference);
    Page<InvoiceRequest> findAll(Pageable pageable);
    Page<InvoiceRequest> findByStatusIn(Pageable pageable, List<RequestStatus> statuses);
    Page<InvoiceRequest> findByMerchantUserReference(Pageable pageable, String userReference);
    long countByStatus(RequestStatus status);
}
