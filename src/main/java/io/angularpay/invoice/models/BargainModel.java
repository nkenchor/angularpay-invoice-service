package io.angularpay.invoice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.invoice.domain.Amount;
import lombok.Data;

@Data
public class BargainModel {

    @JsonProperty("unit_price")
    private Amount setUnitPrice;
}
