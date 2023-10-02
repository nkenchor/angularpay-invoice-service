
package io.angularpay.invoice.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @JsonProperty("bank_account_reference")
    private String bankAccountReference;
    @JsonProperty("investment_status")
    private InvestmentStatus investmentStatus;
    @JsonProperty("user_reference")
    private String userReference;
    @JsonProperty("investment_reference")
    private String investmentReference;
    @JsonProperty("request_created_on")
    private String requestCreatedOn;
    private String reference;
    @JsonProperty("created_on")
    private String createdOn;
}
