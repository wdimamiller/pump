package org.ddmed.pump.composer;

import org.ddmed.pump.PumpApplication;
import org.ddmed.pump.config.PumpConfig;
import org.ddmed.pump.model.Pump;

import org.ddmed.pump.service.PumpRestService;
import org.ddmed.pump.service.PumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.annotation.Resource;


public class ConfigurationComposer extends SelectorComposer {

    @Wire
    Listbox listPumps;
    @Wire
    Bandbox bandPumps;

    @Wire
    Button btnVerify;

    Pump selectedPump ;

    @Autowired
    PumpService pumpService;


    public void initComponents(){


        ListModel<Pump> pumpListModel = new ListModelArray<Pump>(pumpService.getAll());
        listPumps.setModel(pumpListModel);
        listPumps.setItemRenderer((listitem, data, index) -> {
            final Pump pump = (Pump) data;

            Listcell cellName = new Listcell();
            cellName.appendChild(new Label(pump.getName()));
            listitem.appendChild(cellName);

            Listcell cellHostname = new Listcell();
            cellHostname.appendChild(new Label(pump.getDicomHostname()));
            listitem.appendChild(cellHostname);

            Listcell cellPort = new Listcell();
            cellPort.appendChild(new Label(pump.getHttpPort()));
            listitem.appendChild(cellPort);
        });
        //listPumps.setSelectedIndex(0);

    }

    private void init(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(PumpConfig.class);

        selectedPump = context.getBean("currentPUMP", Pump.class);

       // selectedPump =(Pump) listPumps.getListModel().getElementAt(listPumps.getSelectedIndex());
        bandPumps.setText(selectedPump.getName());
    }
    @Listen("onSelect=#listPumps")
    public void fillBand(){

        selectedPump =(Pump) listPumps.getListModel().getElementAt(listPumps.getSelectedIndex());

        bandPumps.setText( selectedPump.getName());
    }

    @Listen("onClick=#btnVerify")
    public void verifyPump(){

        if(PumpRestService.isWorked(selectedPump)){
            Clients.showNotification("Server running, connection verified",
                    Clients.NOTIFICATION_TYPE_INFO,
                    bandPumps, "end_center", 1000);
        }
        else{
            Clients.showNotification("Cant connect to server, " +
                            "check is DCM4CHEE is running or " +
                            "if creadentials are correct",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    bandPumps, "end_center", 100);
        }

    }

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        initComponents();
        init();
    }
}
