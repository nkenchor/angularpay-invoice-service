package io.angularpay.invoice.adapters.inbound;

import io.angularpay.invoice.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.invoice.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.invoice.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.angularpay.invoice.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
public class RedisMessageAdapter implements InboundMessagingPort {

    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}
