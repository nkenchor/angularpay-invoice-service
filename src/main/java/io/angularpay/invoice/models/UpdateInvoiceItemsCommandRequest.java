package io.angularpay.invoice.models;

import io.angularpay.invoice.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateInvoiceItemsCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotEmpty
    private List<Item> items;

    UpdateInvoiceItemsCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
