package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.Role;
import io.angularpay.invoice.exceptions.ErrorObject;
import io.angularpay.invoice.helpers.CommandHelper;
import io.angularpay.invoice.models.GenericCommandResponse;
import io.angularpay.invoice.models.GenericReferenceResponse;
import io.angularpay.invoice.models.UpdateInvoiceSummaryCommandRequest;
import io.angularpay.invoice.validation.DefaultConstraintValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.angularpay.invoice.helpers.CommandHelper.getRequestByReferenceOrThrow;
import static io.angularpay.invoice.helpers.CommandHelper.validRequestStatusOrThrow;

@Slf4j
@Service
public class UpdateInvoiceSummaryCommand extends AbstractCommand<UpdateInvoiceSummaryCommandRequest, GenericReferenceResponse> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final CommandHelper commandHelper;

    public UpdateInvoiceSummaryCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            CommandHelper commandHelper) {
        super("UpdateInvoiceSummaryCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.commandHelper = commandHelper;
    }

    @Override
    protected String getResourceOwner(UpdateInvoiceSummaryCommandRequest request) {
        return this.commandHelper.getRequestOwner(request.getRequestReference());
    }

    @Override
    protected GenericCommandResponse handle(UpdateInvoiceSummaryCommandRequest request) {
        InvoiceRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        validRequestStatusOrThrow(found);
        Supplier<GenericCommandResponse> supplier = () -> updateSummary(request);
        return this.commandHelper.executeAcid(supplier);
    }

    private GenericCommandResponse updateSummary(UpdateInvoiceSummaryCommandRequest request) throws OptimisticLockingFailureException {
        InvoiceRequest found = getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
        return this.commandHelper.updateProperty(found, request::getSummary, found::setSummary);
    }

    @Override
    protected List<ErrorObject> validate(UpdateInvoiceSummaryCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
