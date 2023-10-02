
package io.angularpay.invoice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.invoice.domain.Invoice;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateRequest {

    @NotEmpty
    private String summary;

    @NotNull
    @Valid
    private Invoice invoice;

    @NotNull
    @Valid
    @JsonProperty("customer_reference")
    private String customerReference;
}
