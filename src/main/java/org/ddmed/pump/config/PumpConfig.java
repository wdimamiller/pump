package org.ddmed.pump.config;

import org.ddmed.pump.model.Pump;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class PumpConfig {
    @Bean("currentPUMP")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Pump selectedPump(){

        Pump pump = new Pump();
        pump.setName("TEST PUMP");
        pump.setDicomAETitle("TEST_PUMP");
        pump.setDicomHostname("192.168.2.244");
        pump.setHttpPort("8080");
        pump.setWebUri("dcm4chee-arc");
        pump.setHttpProtocol("http");
        return pump;
    }
}