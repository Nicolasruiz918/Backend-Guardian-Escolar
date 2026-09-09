package com.guardianescolar.api.modules.auth.service;

import com.guardianescolar.api.modules.auth.domain.TwoFactorMethod;

public interface CodeDeliveryService {

    void deliver(String contact, TwoFactorMethod method, String code);
}
