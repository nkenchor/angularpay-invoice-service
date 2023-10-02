package io.angularpay.invoice.ports.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.invoice.models.VerifySignatureResponseModel;

import java.util.Map;

public interface CipherServicePort {
    VerifySignatureResponseModel verifySignature(String requestBody, Map<String, String> headers) throws JsonProcessingException;
}
