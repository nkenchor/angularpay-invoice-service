package io.angularpay.invoice.models;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SupplierModel {

    @NotEmpty
    private int quantity;
}
