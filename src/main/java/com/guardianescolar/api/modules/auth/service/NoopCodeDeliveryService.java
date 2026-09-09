package com.guardianescolar.api.modules.auth.service;

import com.guardianescolar.api.modules.auth.domain.TwoFactorMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NoopCodeDeliveryService implements CodeDeliveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoopCodeDeliveryService.class);

    @Override
    public void deliver(String contact, TwoFactorMethod method, String code) {
        LOGGER.info("Security code generated for {} delivery to {}. External provider pending.", method, contact);
    }
}
