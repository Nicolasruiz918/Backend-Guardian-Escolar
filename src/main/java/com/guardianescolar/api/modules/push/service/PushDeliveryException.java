package com.guardianescolar.api.modules.push.service;

public class PushDeliveryException extends RuntimeException {

    private final boolean invalidDevice;

    public PushDeliveryException(String message, boolean invalidDevice) {
        super(message);
        this.invalidDevice = invalidDevice;
    }

    public PushDeliveryException(String message, Throwable cause) {
        super(message, cause);
        this.invalidDevice = false;
    }

    public boolean isInvalidDevice() {
        return invalidDevice;
    }
}
