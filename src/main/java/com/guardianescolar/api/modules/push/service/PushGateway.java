package com.guardianescolar.api.modules.push.service;

public interface PushGateway {

    void send(PushMessage message);
}
