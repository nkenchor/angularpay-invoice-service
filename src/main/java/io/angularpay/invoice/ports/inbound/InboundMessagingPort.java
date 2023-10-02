package io.angularpay.invoice.ports.inbound;

import io.angularpay.invoice.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}
