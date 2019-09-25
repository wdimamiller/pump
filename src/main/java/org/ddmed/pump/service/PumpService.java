package org.ddmed.pump.service;

import org.ddmed.pump.model.Pump;

import java.util.ArrayList;
import java.util.List;

public class PumpService {

    public static List<Pump> getAll(){

        List<Pump> pumps =  new ArrayList<Pump>();

        Pump pump1 = new Pump();
        pump1.setName("LOCAL PUMP");
        pump1.setDicomAETitle("DCM4CHEE");
        pump1.setDicomHostname("127.0.0.1");
        pump1.setHttpPort("8080");
        pump1.setWebUri("dcm4chee-arc");
        pump1.setHttpProtocol("http");

        pumps.add(pump1);

        Pump pump2 = new Pump();
        pump2.setName("TEST PUMP");
        pump2.setDicomAETitle("TEST_PUMP");
        pump2.setDicomHostname("192.168.2.244");
        pump2.setHttpPort("8080");
        pump2.setWebUri("dcm4chee-arc");
        pump2.setHttpProtocol("http");

        pumps.add(pump2);

        return pumps;
    }
}
