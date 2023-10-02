package io.angularpay.invoice.ports.inbound;

import io.angularpay.invoice.domain.*;
import io.angularpay.invoice.models.*;

import java.util.List;
import java.util.Map;

public interface RestApiPort {
    GenericReferenceResponse createScheduledRequest(String schedule, CreateRequest request, Map<String, String> headers);
    GenericReferenceResponse create(CreateRequest request, Map<String, String> headers);
    void updateSummary(String requestReference, SummaryModel summaryModel, Map<String, String> headers);
    void updateInvoiceItems(String requestReference, List<Item> items, Map<String, String> headers);
    GenericReferenceResponse makePayment(String requestReference, PaymentRequest paymentRequest, Map<String, String> headers);
    void updateRequestStatus(String requestReference, RequestStatusModel status, Map<String, String> headers);
    InvoiceRequest getRequestByReference(String requestReference, Map<String, String> headers);
    List<InvoiceRequest> getNewsfeedModel(int page, Map<String, String> headers);
    List<UserRequestModel> getUserRequests(int page, Map<String, String> headers);
    List<UserInvestmentModel> getUserInvestments(int page, Map<String, String> headers);
    List<InvoiceRequest> getNewsfeedByStatus(int page, List<RequestStatus> statuses, Map<String, String> headers);
    List<InvoiceRequest> getRequestListByStatus(int page, List<RequestStatus> statuses, Map<String, String> headers);
    List<InvoiceRequest> getRequestList(int page, Map<String, String> headers);
    List<Statistics> getStatistics(Map<String, String> headers);
}
