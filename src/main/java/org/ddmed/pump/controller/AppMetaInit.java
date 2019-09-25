package org.ddmed.pump.controller;

import org.ddmed.pump.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;



@Component
class AppMetaInit {

    private static final Logger log = LoggerFactory.getLogger(AppMetaInit.class);

    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {
        log.info("<INFO> CREATIGN ADMIN USER");

        userService.defaultUser();

    }

}