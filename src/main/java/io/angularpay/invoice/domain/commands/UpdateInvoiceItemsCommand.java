package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.domain.Amount;
import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.Role;
import io.angularpay.invoice.exceptions.ErrorObject;
import io.angularpay.invoice.helpers.CommandHelper;
import io.angularpay.invoice.models.GenericCommandResponse;
import io.angularpay.invoice.models.GenericReferenceResponse;
import io.angularpay.invoice.models.UpdateInvoiceItemsCommandRequest;
import io.angularpay.invoice.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.angularpay.invoice.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.invoice.helpers.CommandHelper.validRequestStatusOrThrow;

@Slf4j
@Service
public class UpdateInvoiceItemsCommand extends AbstractCommand<UpdateInvoiceItemsCommandRequest, GenericReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;

    public UpdateInvoiceItemsCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper) {
        super("UpdateInvoiceItemsCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(UpdateInvoiceItemsCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getRequestReference());
    }

    @Override
    protected GenericCommandResponse handle(UpdateInvoiceItemsCommandRequest request) {
        InvoiceRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> updateInvoiceItems(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse updateInvoiceItems(UpdateInvoiceItemsCommandRequest request) throws OptimisticLockingFailureException {
        InvoiceRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());

        BigDecimal totalAmount = request.getItems().stream()
                .map(x-> new BigDecimal(x.getQuantity())
                        .multiply(new BigDecimal(x.getUnitPrice().getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        found.getInvoice().setItems(request.getItems());
        found.getInvoice().setTotalAmount(Amount.builder()
                .value(totalAmount.toPlainString()).currency(request.getItems().get(0).getUnitPrice().getCurrency())
                .build());

        InvoiceRequest response = this.mongoAdapter.updateRequest(found);
        return GenericCommandResponse.builder().requestReference(response.getReference()).build();
    }

    @Override
    protected List<ErrorObject> validate(UpdateInvoiceItemsCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
