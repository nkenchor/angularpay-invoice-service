package io.angularpay.invoice.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T referenceResponse);
}
