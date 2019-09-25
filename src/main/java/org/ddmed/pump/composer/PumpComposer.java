package org.ddmed.pump.composer;

import org.ddmed.pump.configuration.PumpConfig;
import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.service.PumpRestService;
import org.ddmed.pump.service.PumpService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.servlet.http.HttpSession;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PumpComposer extends SelectorComposer {

    @Wire
    private Listbox listPumps;

    @Wire
    private Button btnChoose;

    @Wire
    private Button btnVerify;

    @Wire
    private Button btnAddPump;

    @Wire
    private Window addPumpWindow;

    @Wire
    private Textbox txtNewPumpName;

    @Wire
    private Textbox txtNewPumpHostname;

    @Wire
    private Textbox txtNewPumpHttpPort;

    @Wire
    private Button btnSavePump;


    @WireVariable
    private PumpService pumpService;

    Pump selectedPump ;

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        initComponents();
        init();
    }

    private void initComponents(){

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

    }

    @Listen("onSelect=#listPumps")
    public void fillBand(){

        selectedPump =(Pump) listPumps.getListModel().getElementAt(listPumps.getSelectedIndex());

    }

    @Listen("onClick=#btnChoose")
    public void choosePump(){

        selectedPump =(Pump) listPumps.getListModel().getElementAt(listPumps.getSelectedIndex());
        System.out.println("PUMP THAT SET: " + selectedPump.getName());
        Session session = Executions.getCurrent().getSession();
        session.setAttribute("selectedPump", selectedPump );

    }

    @Listen("onClick=#btnSavePump")
    public void savePump(){

        if((txtNewPumpHostname.getValue().equals(new String("")))
            || (txtNewPumpHttpPort.getValue().equals(new String("")))
            || (txtNewPumpName.getValue().equals(new String(""))) ){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnVerify, "end_center", 100);
        }
        else{
            Pump pump = new Pump(txtNewPumpName.getValue(), txtNewPumpHostname.getValue(), txtNewPumpHttpPort.getValue());
            pumpService.add(pump);
        }

        txtNewPumpHttpPort.setValue(null);
        txtNewPumpHostname.setValue(null);
        txtNewPumpName.setValue(null);
        addPumpWindow.setVisible(false);
    }

    @Listen("onClick=#btnAddPump")
    public void addPump(){

        addPumpWindow.setMode(Window.Mode.MODAL);
        addPumpWindow.setVisible(true);


    }

    @Listen("onClick=#btnVerify")
    public void verifyPump(){

        if(PumpRestService.isWorked(selectedPump)){
            Clients.showNotification("Server running, connection verified",
                    Clients.NOTIFICATION_TYPE_INFO,
                    btnVerify, "end_center", 1000);
        }
        else{
            Clients.showNotification("Cant connect to server, " +
                            "check is DCM4CHEE is running or " +
                            "if creadentials are correct",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnVerify, "end_center", 100);
        }

    }

}



