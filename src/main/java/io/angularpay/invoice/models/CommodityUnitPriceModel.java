package io.angularpay.invoice.models;

import io.angularpay.invoice.domain.Amount;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class CommodityUnitPriceModel {

    @NotEmpty
    private Amount amount;
}
