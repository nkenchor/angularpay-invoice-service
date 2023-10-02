package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.domain.InvestmentStatus;
import io.angularpay.invoice.domain.InvestmentTransactionStatus;
import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.Role;
import io.angularpay.invoice.exceptions.CommandException;
import io.angularpay.invoice.exceptions.ErrorObject;
import io.angularpay.invoice.helpers.CommandHelper;
import io.angularpay.invoice.models.GenericCommandResponse;
import io.angularpay.invoice.models.MakePaymentCommandRequest;
import io.angularpay.invoice.models.ResourceReferenceResponse;
import io.angularpay.invoice.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import static io.angularpay.invoice.exceptions.ErrorCode.REQUEST_COMPLETED_ERROR;
import static io.angularpay.invoice.helpers.CommandHelper.getRequestByReferenceOrThrow;

@Service
public class MakePaymentCommand extends AbstractCommand<MakePaymentCommandRequest, GenericCommandResponse>
        implements ResourceReferenceCommand<GenericCommandResponse, ResourceReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;

    public MakePaymentCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator, CommandHelper commandHelper) {
        super("MakePaymentCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(MakePaymentCommandRequest request) {
        return this.commandHelper.getCustomerOwner(request.getRequestReference());
    }

    @Override
    protected GenericCommandResponse handle(MakePaymentCommandRequest request) {
        InvoiceRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        Supplier<GenericCommandResponse> supplier = () -> makePayment(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse makePayment(MakePaymentCommandRequest request) {
        InvoiceRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        String transactionReference = UUID.randomUUID().toString();

        if (Objects.nonNull(found.getCustomer().getInvestmentStatus())
                && found.getCustomer().getInvestmentStatus().getStatus() == InvestmentTransactionStatus.SUCCESSFUL) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(REQUEST_COMPLETED_ERROR)
                    .message(REQUEST_COMPLETED_ERROR.getDefaultMessage())
                    .build();
        }
        if (Objects.isNull(found.getCustomer().getInvestmentStatus())) {
            found.getCustomer().setInvestmentStatus(InvestmentStatus.builder().status(InvestmentTransactionStatus.PENDING).build());
        }
        // TODO: integrate with transaction service
        //  all of these details should come from transaction service
        found.getCustomer().getInvestmentStatus().setTransactionReference(transactionReference);
        found.getCustomer().getInvestmentStatus().setTransactionDatetime(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        found.getCustomer().getInvestmentStatus().setStatus(InvestmentTransactionStatus.SUCCESSFUL);

        InvoiceRequest response = this.mongoAdapter.updateRequest(found);
        return GenericCommandResponse.builder().requestReference(response.getReference()).itemReference(transactionReference).build();
    }

    @Override
    protected List<ErrorObject> validate(MakePaymentCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }

    @Override
    public ResourceReferenceResponse map(GenericCommandResponse genericCommandResponse) {
        return new ResourceReferenceResponse(genericCommandResponse.getItemReference());
    }
}
