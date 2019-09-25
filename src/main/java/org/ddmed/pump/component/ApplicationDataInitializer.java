package org.ddmed.pump.component;

import org.ddmed.pump.service.PumpService;
import org.ddmed.pump.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.lang.Library;

import javax.annotation.PostConstruct;

@Component
class ApplicationDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationDataInitializer.class);

    private final UserService userService;
    private final PumpService pumpService;

    @Autowired
    ApplicationDataInitializer(UserService userService, PumpService pumpService) {
        this.userService = userService;
        this.pumpService = pumpService;
    }

    @PostConstruct
    private void init() {
        log.info("Creating default admin user");
        userService.createDefaultUser();
        log.info("Creating default local pump");
        pumpService.createDefaultPump();
        log.info("Set prefered theme ");
        Library.setProperty("org.zkoss.theme.preferred", "sapphire");
    }

}