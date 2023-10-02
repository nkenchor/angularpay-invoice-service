package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.domain.Customer;
import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.Role;
import io.angularpay.invoice.exceptions.ErrorObject;
import io.angularpay.invoice.models.GetUserInvestmentsCommandRequest;
import io.angularpay.invoice.models.UserInvestmentModel;
import io.angularpay.invoice.validation.DefaultConstraintValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GetUserInvestmentsCommand extends AbstractCommand<GetUserInvestmentsCommandRequest, List<UserInvestmentModel>> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetUserInvestmentsCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator) {
        super("GetUserInvestmentsCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetUserInvestmentsCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<UserInvestmentModel> handle(GetUserInvestmentsCommandRequest request) {
        Pageable pageable = PageRequest.of(request.getPaging().getIndex(), request.getPaging().getSize());
        List<UserInvestmentModel> investmentRequests = new ArrayList<>();
        List<InvoiceRequest> response = this.mongoAdapter.listRequests(pageable).getContent();
        for (InvoiceRequest invoiceRequest : response) {
            Customer customer = invoiceRequest.getCustomer();
            if (request.getAuthenticatedUser().getUserReference().equalsIgnoreCase(customer.getUserReference())) {
                investmentRequests.add(UserInvestmentModel.builder()
                        .requestReference(invoiceRequest.getReference())
                        .investmentReference(customer.getReference())
                        .userReference(customer.getUserReference())
                        .requestCreatedOn(customer.getCreatedOn())
                        .build());
            }
        }
        return investmentRequests;
    }

    @Override
    protected List<ErrorObject> validate(GetUserInvestmentsCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
