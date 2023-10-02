package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.domain.Role;
import io.angularpay.invoice.exceptions.ErrorObject;
import io.angularpay.invoice.models.GetUserRequestsCommandRequest;
import io.angularpay.invoice.models.UserRequestModel;
import io.angularpay.invoice.validation.DefaultConstraintValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetUserRequestsCommand extends AbstractCommand<GetUserRequestsCommandRequest, List<UserRequestModel>> {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;

    public GetUserRequestsCommand(ObjectMapper mapper, MongoAdapter mongoAdapter, DefaultConstraintValidator validator) {
        super("GetUserRequestsCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
    }

    @Override
    protected String getResourceOwner(GetUserRequestsCommandRequest request) {
        return request.getAuthenticatedUser().getUserReference();
    }

    @Override
    protected List<UserRequestModel> handle(GetUserRequestsCommandRequest request) {
        Pageable pageable = PageRequest.of(request.getPaging().getIndex(), request.getPaging().getSize());
        return this.mongoAdapter.findByMerchantUserReference(pageable, request.getAuthenticatedUser().getUserReference())
                .getContent().stream()
                .map(x -> UserRequestModel.builder()
                        .requestReference(x.getReference())
                        .userReference(x.getMerchant().getUserReference())
                        .requestCreatedOn(x.getCreatedOn())
                        .build()).collect(Collectors.toList());
    }

    @Override
    protected List<ErrorObject> validate(GetUserRequestsCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Collections.emptyList();
    }
}
