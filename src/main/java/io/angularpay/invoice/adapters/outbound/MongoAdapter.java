package io.angularpay.invoice.adapters.outbound;

import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.RequestStatus;
import io.angularpay.invoice.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final InvoiceRepository invoiceRepository;

    @Override
    public InvoiceRequest createRequest(InvoiceRequest request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return invoiceRepository.save(request);
    }

    @Override
    public InvoiceRequest updateRequest(InvoiceRequest request) {
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return invoiceRepository.save(request);
    }

    @Override
    public Optional<InvoiceRequest> findRequestByReference(String reference) {
        return invoiceRepository.findByReference(reference);
    }

    @Override
    public Page<InvoiceRequest> listRequests(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    public Page<InvoiceRequest> findRequestsByStatus(Pageable pageable, List<RequestStatus> statuses) {
        return invoiceRepository.findByStatusIn(pageable, statuses);
    }

    @Override
    public Page<InvoiceRequest> findByMerchantUserReference(Pageable pageable, String userReference) {
        return invoiceRepository.findByMerchantUserReference(pageable, userReference);
    }

    @Override
    public long getCountByRequestStatus(RequestStatus status) {
        return invoiceRepository.countByStatus(status);
    }

    @Override
    public long getTotalCount() {
        return invoiceRepository.count();
    }
}
