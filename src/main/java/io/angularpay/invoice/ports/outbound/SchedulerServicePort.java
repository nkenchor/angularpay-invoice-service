package io.angularpay.invoice.ports.outbound;

import io.angularpay.invoice.models.SchedulerServiceRequest;
import io.angularpay.invoice.models.SchedulerServiceResponse;

import java.util.Map;
import java.util.Optional;

public interface SchedulerServicePort {
    Optional<SchedulerServiceResponse> createScheduledRequest(SchedulerServiceRequest request, Map<String, String> headers);
}
