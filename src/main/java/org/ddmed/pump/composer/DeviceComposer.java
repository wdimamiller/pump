package org.ddmed.pump.composer;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.model.Device;
import org.ddmed.pump.service.PumpRestService;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DeviceComposer extends SelectorComposer {

    @Wire
    private Listbox listDevices;
    @Wire
    private Button btnAddDevice;

    @Wire
    private Textbox txtEditDeviceName;
    @Wire
    private Textbox txtEditDeviceAETitle;
    @Wire
    private Textbox txtEditDeviceHostname;
    @Wire
    private Textbox txtEditDeviceDicomPort;

    @Wire
    private Button btnDelete;

    @Wire
    private Button btnSave;

    @Wire
    private Button btnVerify;

    @Wire
    private Button btnEdit;



    @Wire("#windowCreateDevice  #txtNewDeviceName")
    private Textbox txtNewDeviceName;
    @Wire("#windowCreateDevice  #txtNewDeviceAETitle")
    private Textbox txtNewDeviceAETitle;
    @Wire("#windowCreateDevice  #txtNewDeviceHostname")
    private Textbox txtNewDeviceHostname;
    @Wire("#windowCreateDevice  #txtNewDeviceDicomPort")
    private Textbox txtNewDeviceDicomPort;

    @Wire("#windowCreateDevice  #btnSaveNewDevice")
    private Button btnSaveNewDevice;
    @Wire("#windowCreateDevice")
    private Window windowCreateDevice;

    private Pump selectedPump;
    private Device selectedDevice;

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init(){

        Session session = Executions.getCurrent().getSession();
        selectedPump = (Pump) session.getAttribute("selectedPump");
        selectedDevice = null;

        disableEditPanel(true);
        disableButtons(true);
        fillListDevices();
        clearEditPanel();

    }

    private void fillListDevices(){
        ListModel<Device> deviceListModel = new ListModelArray<Device>(PumpRestService.getAllDevices(selectedPump, true));
        listDevices.setModel(deviceListModel);
        listDevices.setItemRenderer((listitem, data, index) -> {
            final Device device = (Device) data;

            Listcell devName = new Listcell();
            devName.appendChild(new Label(device.getName()));
            listitem.appendChild(devName);

            Listcell deviceAETitle = new Listcell();
            deviceAETitle.appendChild(new Label(device.getAETitle()));
            listitem.appendChild(deviceAETitle);

            Listcell devHostname = new Listcell();
            devHostname.appendChild(new Label(device.getHostname()));
            listitem.appendChild(devHostname);

            Listcell deviceDicomPort = new Listcell();
            deviceDicomPort.appendChild(new Label(device.getDicomPort()));
            listitem.appendChild(deviceDicomPort);

        });
    }

    @Listen("onClick=#btnAddDevice")
    public void setBtnAddDevice(){

        windowCreateDevice.setVisible(true);
        windowCreateDevice.setMode(Window.MODAL);
    }

    @Listen("onClick=window #btnSaveNewDevice")
    public void btnSaveNewDevice(){
        if(!isValideNewDevice()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSaveNewDevice, "end_center", 2000);
            return;
        }
        Device device = new Device();
        device.setName(txtNewDeviceName.getValue());
        device.setAETitle(txtNewDeviceAETitle.getValue());
        device.setHostname(txtNewDeviceHostname.getValue());
        device.setDicomPort(txtNewDeviceDicomPort.getValue());
        int status = PumpRestService.addDevice(selectedPump, device);
        if(status == 200 || status == 204){
            Clients.showNotification("New AETitle successfully added",
                    Clients.NOTIFICATION_TYPE_INFO,
                    btnSaveNewDevice, "end_center", 2000);

            closeWindowCreateDevice();
            init();

        }
        else{
            if(status==409){
                Clients.showNotification("Device, Application Entity, HL7 Application or Web Application already exists",
                        Clients.NOTIFICATION_TYPE_ERROR,
                        btnSaveNewDevice, "end_center", 2000);
            }
            else{
                Clients.showNotification("Cant add current combination",
                        Clients.NOTIFICATION_TYPE_ERROR,
                        btnSaveNewDevice, "end_center", 2000);
            }
        }

    }

    private boolean isValideNewDevice(){
        if(txtNewDeviceName.getValue().equals("") || txtNewDeviceName.getValue() == null
                || txtNewDeviceAETitle.getValue().equals("") || txtNewDeviceAETitle.getValue() == null
                || txtNewDeviceHostname.getValue().equals("") || txtNewDeviceHostname.getValue() == null
                || txtNewDeviceDicomPort.getValue().equals("") || txtNewDeviceDicomPort.getValue() == null
                ){
            return false;
        }
        return true;
    }

    private boolean isValideEditDevice(){
        if(txtEditDeviceName.getValue().equals("") || txtEditDeviceName.getValue() == null
                || txtEditDeviceAETitle.getValue().equals("") || txtEditDeviceAETitle.getValue() == null
                || txtEditDeviceHostname.getValue().equals("") || txtEditDeviceHostname.getValue() == null
                || txtEditDeviceDicomPort.getValue().equals("") || txtEditDeviceDicomPort.getValue() == null
        ){
            return false;
        }
        return true;
    }

    @Listen("onClick=window #btnCancelNewDevice")
    public void btnClearNewPump(){
        closeWindowCreateDevice();
    }

    public void closeWindowCreateDevice(){

        txtNewDeviceName.setValue(null);
        txtNewDeviceAETitle.setValue(null);
        txtNewDeviceHostname.setValue(null);
        txtNewDeviceDicomPort.setValue(null);

        windowCreateDevice.setMode(Window.Mode.HIGHLIGHTED);
        windowCreateDevice.setVisible(false);
    }

    private void fillEditPanel(){
        txtEditDeviceName.setValue(selectedDevice.getName());
        txtEditDeviceAETitle.setValue(selectedDevice.getAETitle());
        txtEditDeviceHostname.setValue(selectedDevice.getHostname());
        txtEditDeviceDicomPort.setValue(selectedDevice.getDicomPort());
    }

    private void clearEditPanel(){
        txtEditDeviceName.setValue(null);
        txtEditDeviceAETitle.setValue(null);
        txtEditDeviceHostname.setValue(null);
        txtEditDeviceDicomPort.setValue(null);
    }

    private void disableEditPanel(boolean flag){
        txtEditDeviceName.setDisabled(true);
        txtEditDeviceAETitle.setDisabled(flag);
        txtEditDeviceHostname.setDisabled(flag);
        txtEditDeviceDicomPort.setDisabled(flag);
        if(flag){
            btnSave.setVisible(false);
        }
        else{
            btnSave.setVisible(true);
        }
    }
    private void disableButtons(boolean flag){
        btnDelete.setDisabled(flag);
        btnEdit.setDisabled(flag);
        btnVerify.setDisabled(flag);
    }

    @Listen("onSelect=#listDevices")
    public void fillBand(){

        selectedDevice = (Device) listDevices.getListModel().getElementAt(listDevices.getSelectedIndex());
        fillEditPanel();
        disableButtons(false);

    }

    @Listen("onClick=#btnVerify")
    public void btnVerifyClick(){

        if(!isValideEditDevice()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnVerify, "end_center", 100);
        }
        else{
            Device device = new Device();
            device.setName(txtEditDeviceName.getValue());
            device.setHostname(txtEditDeviceHostname.getValue());
            device.setAETitle(txtEditDeviceAETitle.getValue());
            device.setDicomPort(txtEditDeviceDicomPort.getValue());

            if (PumpRestService.verifyDevice(selectedPump, device)) {
                Clients.showNotification("Connection verified",
                        Clients.NOTIFICATION_TYPE_INFO,
                        btnVerify, "end_center", 1000);
            }
            else{
                Clients.showNotification("Cant connect",
                        Clients.NOTIFICATION_TYPE_ERROR,
                        btnVerify, "end_center", 100);
            }

        }

    }

    @Listen("onClick=#btnDelete")
    public void deleteSelected(){
        Messagebox.show("Are you realy want to delete " + selectedDevice.getName() +"?", "Deleting", Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION, new EventListener<Event>() {
                    @Override
                    public void onEvent(final Event evt) throws InterruptedException {
                        if (Messagebox.ON_YES.equals(evt.getName())) {
                            PumpRestService.deleteDevice(selectedPump, selectedDevice);
                            init();
                        } else {

                        }
                    }
                }
        );
    }

    @Listen("onClick=#btnSave")
    public void btnSaveClick(){

        if(!isValideEditDevice()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSave, "end_center", 100);
        }
        else{

            selectedDevice.setName(txtEditDeviceName.getValue());
            selectedDevice.setHostname(txtEditDeviceHostname.getValue());
            selectedDevice.setAETitle(txtEditDeviceAETitle.getValue());
            selectedDevice.setDicomPort(txtEditDeviceDicomPort.getValue());



            PumpRestService.editDevice(selectedPump, selectedDevice);
            fillListDevices();
            Clients.showNotification("Saved!",
                    Clients.NOTIFICATION_TYPE_INFO,
                    btnSave, "end_center", 100);

            init();
        }

    }

    @Listen("onClick=#btnEdit")
    public void btnEditClick(){
        disableEditPanel(false);
    }





}