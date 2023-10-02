package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.Role;
import io.angularpay.invoice.exceptions.ErrorObject;
import io.angularpay.invoice.models.GetRequestByReferenceCommandRequest;
import io.angularpay.invoice.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static io.angularpay.invoice.helpers.CommandHelper.getRequestByReferenceOrThrow;

@Service
public class GetRequestByReferenceCommand extends AbstractCommand<GetRequestByReferenceCommandRequest, InvoiceRequest> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetRequestByReferenceCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator) {
        super("GetRequestByReferenceCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetRequestByReferenceCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected InvoiceRequest handle(GetRequestByReferenceCommandRequest request) {
        return getRequestByReferenceOrThrow(this.mongoAdapter, request.getRequestReference());
    }

    @Override
    protected List<ErrorObject> validate(GetRequestByReferenceCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
