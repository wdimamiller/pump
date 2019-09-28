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


@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PumpComposer extends SelectorComposer {

    @Wire
    private Listbox listPumps;
    @Wire
    private Button btnMakeDefault;
    @Wire
    private Button btnVerify;
    @Wire
    private Button btnEdit;
    @Wire
    private Button btnSave;
    @Wire
    private Button btnAddPump;
    @Wire
    private Vbox addPumpWindow;

    @Wire
    private Textbox txtEditPumpName;
    @Wire
    private Textbox txtEditPumpHostname;
    @Wire
    private Textbox txtEditPumpHttpPort;
    @Wire
    private Textbox txtEditPumpAETitle;
    @Wire
    private Textbox txtEditPumpDicomPort;


    @Wire("#windowCreatePump  #txtNewPumpName")
    private Textbox txtNewPumpName;
    @Wire("#windowCreatePump  #txtNewPumpHostname")
    private Textbox txtNewPumpHostname;
    @Wire("#windowCreatePump  #txtNewPumpHttpPort")
    private Textbox txtNewPumpHttpPort;
    @Wire("#windowCreatePump  #txtNewPumpAETitle")
    private Textbox txtNewPumpAETitle;
    @Wire("#windowCreatePump  #txtNewPumpDicomPort")
    private Textbox txtNewPumpDicomPort;
    @Wire("#windowCreatePump  #btnSaveNewPump")
    private Button btnSaveNewPump;
    @Wire("#windowCreatePump  #btnVeriyNewPump")
    private Button btnVeriyNewPump;
    @Wire("#windowCreatePump")
    private Window windowCreatePump;


    @WireVariable
    private PumpService pumpService;

    Pump selectedPump;

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();

    }

    private void fillListPump(){
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

            Listcell cellAETitle = new Listcell();
            cellAETitle.appendChild(new Label(pump.getDicomAETitle()));
            listitem.appendChild(cellAETitle);

            Listcell cellDicomPort = new Listcell();
            cellDicomPort.appendChild(new Label(pump.getDicomPort()));
            listitem.appendChild(cellDicomPort);

            Listcell cellPort = new Listcell();
            cellPort.appendChild(new Label(pump.getHttpPort()));
            listitem.appendChild(cellPort);

            Listcell cellDefault = new Listcell();

            String strDefault = "";
            if(pump.isUseByDefault()) {
                strDefault = "TRUE";
            }
            cellDefault.appendChild(new Label(strDefault));
            listitem.appendChild(cellDefault);

        });
    }

    private void init(){
        fillListPump();
        disableEditPanel(true);
    }

    private void fillEditPanel(){
        txtEditPumpName.setValue(selectedPump.getName());
        txtEditPumpAETitle.setValue(selectedPump.getDicomAETitle());
        txtEditPumpHostname.setValue(selectedPump.getDicomHostname());
        txtEditPumpDicomPort.setValue(selectedPump.getDicomPort());
        txtEditPumpHttpPort.setValue(selectedPump.getHttpPort());
    }

    private void clearEditPanel(){
        txtEditPumpName.setValue(null);
        txtEditPumpAETitle.setValue(null);
        txtEditPumpHostname.setValue(null);
        txtEditPumpDicomPort.setValue(null);
        txtEditPumpHttpPort.setValue(null);
    }

    private void disableEditPanel(boolean flag){
        txtEditPumpName.setDisabled(flag);
        txtEditPumpAETitle.setDisabled(flag);
        txtEditPumpHostname.setDisabled(flag);
        txtEditPumpDicomPort.setDisabled(flag);
        txtEditPumpHttpPort.setDisabled(flag);
        if(flag){
            btnSave.setVisible(false);
        }
        else{
            btnSave.setVisible(true);
        }
    }
    private void disableButtons(boolean flag){
        btnEdit.setDisabled(flag);
        btnMakeDefault.setDisabled(flag);
        btnVerify.setDisabled(flag);
    }

    @Listen("onSelect=#listPumps")
    public void fillBand(){

        selectedPump = (Pump) listPumps.getListModel().getElementAt(listPumps.getSelectedIndex());
        fillEditPanel();
        disableButtons(false);

    }

    @Listen("onClick=#btnVerify")
    public void btnVerifyClick(){

        if(!isValideEditedPump()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnVerify, "end_center", 100);
        }
        else{
            Pump pump = new Pump(txtEditPumpName.getValue(), txtEditPumpHostname.getValue(), txtEditPumpHttpPort.getValue(), txtEditPumpAETitle.getValue(), txtEditPumpDicomPort.getValue());

            if (verifyPump(pump)) {
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

    @Listen("onClick=#btnSave")
    public void btnSaveClick(){

        if(!isValideEditedPump()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSave, "end_center", 100);
        }
        else{

            selectedPump.setName(txtEditPumpName.getValue());
            selectedPump.setDicomHostname(txtEditPumpHostname.getValue());
            selectedPump.setDicomAETitle(txtEditPumpAETitle.getValue());
            selectedPump.setDicomPort(txtEditPumpDicomPort.getValue());
            selectedPump.setHttpPort(txtEditPumpHttpPort.getValue());


            pumpService.add(selectedPump);
            fillListPump();
            Clients.showNotification("Saved!",
                    Clients.NOTIFICATION_TYPE_INFO,
                    btnSave, "end_center", 100);

            disableEditPanel(true);
            if(selectedPump.isUseByDefault()){
                setBtnMakeDefault();
            }
        }

    }

    @Listen("onClick=#btnMakeDefault")
    public void setBtnMakeDefault(){

        Pump currentDefaultPump = pumpService.getDefault();
        currentDefaultPump.setUseByDefault(false);
        pumpService.save(currentDefaultPump);

        selectedPump.setUseByDefault(true);
        pumpService.save(selectedPump);

        fillListPump();

        Clients.showNotification("Changed!",
                Clients.NOTIFICATION_TYPE_INFO,
                btnMakeDefault, "end_center", 100);

        Session session = Executions.getCurrent().getSession();
        session.setAttribute("selectedPump", selectedPump);

        session = Executions.getCurrent().getSession();
        MainTabComposer main = (MainTabComposer) session.getAttribute("mainTab");
        main.checkPump();

        session = Executions.getCurrent().getSession();
        FilterComposer homeTab = (FilterComposer) session.getAttribute("homeTab");
        homeTab.init();

        Executions.getCurrent().sendRedirect("/");
    }

    @Listen("onClick=#btnEdit")
    public void btnEditClick(){
        disableEditPanel(false);
    }

    @Listen("onClick=#btnAddPump")
    public void addPump(){

        windowCreatePump.setVisible(true);
        windowCreatePump.setMode(Window.MODAL);
    }


    public boolean verifyPump(Pump pump){

        if(PumpRestService.isWorked(pump)){
            return true;
        }
        return false;

    }
    @Listen("onClick=window #btnSaveNewPump")
    public void btnSaveNewPump(){

        if(!isValideNewPump()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSaveNewPump, "end_center", 100);
        }
        else{
            Pump pump = new Pump(txtNewPumpName.getValue(), txtNewPumpHostname.getValue(), txtNewPumpHttpPort.getValue(), txtNewPumpAETitle.getValue(), txtNewPumpDicomPort.getValue());
            pumpService.add(pump);
            fillListPump();
            Clients.showNotification("Saved!",
                    Clients.NOTIFICATION_TYPE_INFO,
                    btnSaveNewPump, "end_center", 100);

            closeWindowCreatePump();
        }
    }


    @Listen("onClick=window #btnCancelNewPump")
    public void btnClearNewPump(){
        closeWindowCreatePump();
    }

    @Listen("onClick=window #btnVeriyNewPump")
    public void btnVerifyNewPump(){

        if(!isValideNewPump()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSaveNewPump, "end_center", 100);
        }
        else{
            Pump pump = new Pump(txtNewPumpName.getValue(), txtNewPumpHostname.getValue(), txtNewPumpHttpPort.getValue(), txtNewPumpAETitle.getValue(), txtNewPumpDicomPort.getValue());

            if (verifyPump(pump)) {
                Clients.showNotification("Server running, connection verified",
                        Clients.NOTIFICATION_TYPE_INFO,
                        btnVeriyNewPump, "end_center", 1000);
            }
            else{
                Clients.showNotification("Cant connect to server, " +
                                "check is DCM4CHEE is running or " +
                                "if creadentials are correct",
                        Clients.NOTIFICATION_TYPE_ERROR,
                        btnVeriyNewPump, "end_center", 100);
            }

        }
    }

    public boolean isValideNewPump(){
        if(txtNewPumpName.getValue().equals("") || txtNewPumpName.getValue() == null
                || txtNewPumpHostname.getValue().equals("") || txtNewPumpHostname.getValue() == null
                || txtNewPumpDicomPort.getValue().equals("") || txtNewPumpDicomPort.getValue() == null
                || txtNewPumpAETitle.getValue().equals("") || txtNewPumpAETitle.getValue() == null
                || txtNewPumpHttpPort.getValue().equals("")|| txtNewPumpHttpPort.getValue() == null){
            return false;
        }
       return true;
    }

    public boolean isValideEditedPump(){
        if(txtEditPumpName.getValue().equals("") || txtEditPumpName.getValue() == null
                || txtEditPumpHostname.getValue().equals("") || txtEditPumpHostname.getValue() == null
                || txtEditPumpDicomPort.getValue().equals("") || txtEditPumpDicomPort.getValue() == null
                || txtEditPumpAETitle.getValue().equals("") || txtEditPumpAETitle.getValue() == null
                || txtEditPumpHttpPort.getValue().equals("")|| txtEditPumpHttpPort.getValue() == null){
            return false;
        }
        return true;
    }

    public void closeWindowCreatePump(){

        txtNewPumpName.setValue(null);
        txtNewPumpHostname.setValue(null);
        txtNewPumpHttpPort.setValue(null);
        txtNewPumpAETitle.setValue(null);
        txtNewPumpDicomPort.setValue(null);
        windowCreatePump.setMode(Window.Mode.HIGHLIGHTED);
        windowCreatePump.setVisible(false);
    }
}



