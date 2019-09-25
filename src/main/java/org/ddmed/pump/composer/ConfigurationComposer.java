package org.ddmed.pump.composer;

import org.ddmed.pump.configuration.PumpConfig;
import org.ddmed.pump.domain.Pump;

import org.ddmed.pump.service.PumpRestService;
import org.ddmed.pump.service.PumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ConfigurationComposer extends SelectorComposer {

    @Wire
    private Listbox listPumps;
    @Wire
    private Bandbox bandPumps;
    @Wire
    private Button btnVerify;

    @WireVariable
    private PumpService pumpService;

    private Pump selectedPump;

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


    }

    private void init(){



        //globalPump =(Pump) listPumps.getListModel().getElementAt(listPumps.getSelectedIndex());
        //bandPumps.setText(selectedPump.getName());
    }



    @Listen("onSelect=#listPumps")
    public void fillBand(){

        selectedPump =(Pump) listPumps.getListModel().getElementAt(listPumps.getSelectedIndex());


      //  bandPumps.setText( selectedPump.getName());
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
