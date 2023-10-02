package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.domain.*;
import io.angularpay.invoice.exceptions.ErrorObject;
import io.angularpay.invoice.models.CreateRequestCommandRequest;
import io.angularpay.invoice.models.GenericCommandResponse;
import io.angularpay.invoice.models.GenericReferenceResponse;
import io.angularpay.invoice.models.ResourceReferenceResponse;
import io.angularpay.invoice.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.angularpay.invoice.helpers.ObjectFactory.pmtRequestWithDefaults;

@Slf4j
@Service
public class CreateRequestCommand extends AbstractCommand<CreateRequestCommandRequest, GenericReferenceResponse>
        implements ResourceReferenceCommand<GenericCommandResponse, ResourceReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public CreateRequestCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator) {
        super("CreateRequestCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(CreateRequestCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected GenericCommandResponse handle(CreateRequestCommandRequest request) {
        BigDecimal totalAmount = request.getCreateRequest().getInvoice().getItems().stream()
                .map(x-> new BigDecimal(x.getQuantity())
                        .multiply(new BigDecimal(x.getUnitPrice().getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        request.getCreateRequest().getInvoice().setTotalAmount(Amount.builder()
                .value(totalAmount.toPlainString()).currency(request.getCreateRequest().getInvoice().getItems().get(0).getUnitPrice().getCurrency())
                .build());

        InvoiceRequest invoiceRequestWithDefaults = pmtRequestWithDefaults();
        InvoiceRequest withOtherDetails = invoiceRequestWithDefaults.toBuilder()
                .summary(request.getCreateRequest().getSummary())
                .invoice(request.getCreateRequest().getInvoice())
                .merchant(Merchant.builder()
                        .userReference(request.getAuthenticatedUser().getUserReference())
                        .build())
                .customer(Customer.builder()
                        .userReference(request.getCreateRequest().getCustomerReference())
                        .reference(UUID.randomUUID().toString())
                        .createdOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
                        .build())
                .build();
        InvoiceRequest response = this.mongoAdapter.createRequest(withOtherDetails);
        return GenericCommandResponse.builder().requestReference(response.getReference()).build();
    }

    @Override
    protected List<ErrorObject> validate(CreateRequestCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

    @Override
    public ResourceReferenceResponse map(GenericCommandResponse genericCommandResponse) {
        return new ResourceReferenceResponse(genericCommandResponse.getRequestReference());
    }
}
