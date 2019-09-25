package org.ddmed.pump.service;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.repository.PumpRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PumpService {


    private final PumpRepository pumpRepository;

    public PumpService(PumpRepository pumpRepository) {
        this.pumpRepository = pumpRepository;
    }

    public void createDefaultPump(){

        Pump pump = new Pump();
        pump.setName("LOCAL PUMP");
        pump.setDicomAETitle("DCM4CHEE");
        pump.setDicomHostname("127.0.0.1");
        pump.setHttpPort("8080");
        pump.setWebUri("dcm4chee-arc");
        pump.setHttpProtocol("http");
        pump.setUseByDefault(true);

        pumpRepository.save(pump);
    }

    public List<Pump> getAll(){

        return (List<Pump>)pumpRepository.findAll();

    }
}
