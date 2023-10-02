package io.angularpay.invoice.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.invoice.adapters.outbound.MongoAdapter;
import io.angularpay.invoice.configurations.AngularPayConfiguration;
import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.domain.RequestStatus;
import io.angularpay.invoice.exceptions.CommandException;
import io.angularpay.invoice.exceptions.ErrorCode;
import io.angularpay.invoice.models.GenericCommandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.angularpay.invoice.exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommandHelper {

    private final MongoAdapter mongoAdapter;
    private final ObjectMapper mapper;
    private final AngularPayConfiguration configuration;

    public GenericCommandResponse executeAcid(Supplier<GenericCommandResponse> supplier) {
        int maxRetry = this.configuration.getMaxUpdateRetry();
        OptimisticLockingFailureException optimisticLockingFailureException;
        int counter = 0;
        //noinspection ConstantConditions
        do {
            try {
                return supplier.get();
            } catch (OptimisticLockingFailureException exception) {
                if (counter++ >= maxRetry) throw exception;
                optimisticLockingFailureException = exception;
            }
        }
        while (Objects.nonNull(optimisticLockingFailureException));
        throw optimisticLockingFailureException;
    }

    public String getRequestOwner(String requestReference) {
        InvoiceRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        return found.getMerchant().getUserReference();
    }

    private static CommandException commandException(HttpStatus status, ErrorCode errorCode) {
        return CommandException.builder()
                .status(status)
                .errorCode(errorCode)
                .message(errorCode.getDefaultMessage())
                .build();
    }

    public <T> GenericCommandResponse updateProperty(InvoiceRequest invoiceRequest, Supplier<T> getter, Consumer<T> setter) {
        setter.accept(getter.get());
        InvoiceRequest response = this.mongoAdapter.updateRequest(invoiceRequest);
        return GenericCommandResponse.builder().requestReference(response.getReference()).build();
    }

    public <T> String toJsonString(T t) throws JsonProcessingException {
        return this.mapper.writeValueAsString(t);
    }

    public static InvoiceRequest getRequestByReferenceOrThrow(MongoAdapter mongoAdapter, String requestReference) {
        return mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
    }

    public static void validRequestStatusOrThrow(InvoiceRequest found) {
        if (found.getStatus() == RequestStatus.COMPLETED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_COMPLETED_ERROR);
        }
        if (found.getStatus() == RequestStatus.CANCELLED) {
            throw commandException(HttpStatus.UNPROCESSABLE_ENTITY, REQUEST_CANCELLED_ERROR);
        }
    }

    public String getCustomerOwner(String requestReference) {
        InvoiceRequest found = this.mongoAdapter.findRequestByReference(requestReference).orElseThrow(
                () -> commandException(HttpStatus.NOT_FOUND, REQUEST_NOT_FOUND)
        );
        return found.getCustomer().getUserReference();
    }
}
