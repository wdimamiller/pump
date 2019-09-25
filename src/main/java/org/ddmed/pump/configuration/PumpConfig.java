package org.ddmed.pump.configuration;

import org.ddmed.pump.domain.Pump;
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
        pump.setDicomHostname("");
        pump.setHttpPort("");
        pump.setWebUri("dcm4chee-arc");
        pump.setHttpProtocol("http");
        return pump;
    }
}