package io.angularpay.invoice.domain.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.angularpay.invoice.adapters.outbound.RedisAdapter;
import io.angularpay.invoice.domain.InvoiceRequest;
import io.angularpay.invoice.models.UserNotificationBuilderParameters;
import io.angularpay.invoice.models.UserNotificationType;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public interface UserNotificationsPublisherCommand<T extends InvoiceRequestSupplier> {

    RedisAdapter getRedisAdapter();
    UserNotificationType getUserNotificationType(T commandResponse);
    List<String> getAudience(T commandResponse);
    String convertToUserNotificationsMessage(UserNotificationBuilderParameters<T, InvoiceRequest> parameters) throws JsonProcessingException;

    default void publishUserNotification(T commandResponse) {
        InvoiceRequest request = commandResponse.getInvoiceRequest();
        RedisAdapter redisAdapter = this.getRedisAdapter();
        UserNotificationType type = this.getUserNotificationType(commandResponse);
        List<String> audience = this.getAudience(commandResponse);

        if (Objects.nonNull(request) && Objects.nonNull(redisAdapter)
        && Objects.nonNull(type) && !CollectionUtils.isEmpty(audience)) {
            audience.stream().parallel().forEach(userReference-> {
                try {
                    UserNotificationBuilderParameters<T, InvoiceRequest> parameters = UserNotificationBuilderParameters.<T, InvoiceRequest>builder()
                            .userReference(userReference)
                            .request(request)
                            .commandResponse(commandResponse)
                            .type(type)
                            .build();
                    String message = this.convertToUserNotificationsMessage(parameters);
                    redisAdapter.publishUserNotification(message);
                } catch (JsonProcessingException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }
}
