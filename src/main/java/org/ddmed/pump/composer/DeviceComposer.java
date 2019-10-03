package org.ddmed.pump.composer;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.model.Device;
import org.ddmed.pump.service.PumpRestService;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
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
    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {

        super.doAfterCompose(comp);
        init();

    }

    private void init(){

        Session session = Executions.getCurrent().getSession();
        selectedPump = (Pump) session.getAttribute("selectedPump");

        fillListDevices();

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

    @Listen("onSelect=#listDevices")
    public void selectDevice(){

    }

    @Listen("onClick=#btnAddDevice")
    public void addPump(){

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

        int status = PumpRestService.addDevice(selectedPump, txtNewDeviceName.getValue(), txtNewDeviceAETitle.getValue(), txtNewDeviceHostname.getValue(), txtNewDeviceDicomPort.getValue());
        if(status == 200 || status == 204){
            Clients.showNotification("New AETitle successfully added",
                    Clients.NOTIFICATION_TYPE_INFO,
                    btnSaveNewDevice, "end_center", 2000);

            closeWindowCreatePump();
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

    @Listen("onClick=window #btnCancelNewDevice")
    public void btnClearNewPump(){
        closeWindowCreatePump();
    }



    public void closeWindowCreatePump(){

        txtNewDeviceName.setValue(null);
        txtNewDeviceAETitle.setValue(null);
        txtNewDeviceHostname.setValue(null);
        txtNewDeviceDicomPort.setValue(null);

        windowCreateDevice.setMode(Window.Mode.HIGHLIGHTED);
        windowCreateDevice.setVisible(false);
    }
}