package org.ddmed.pump.composer;


import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.model.Device;
import org.ddmed.pump.model.Exporter;
import org.ddmed.pump.service.LDAPService;
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
public class ExporterComposer extends SelectorComposer {

    @Wire
    private Listbox listExporters;
    @Wire
    private Button btnAddExporter;
    @Wire("#windowCreateExporter  #txtNewExporterID")
    private Textbox txtNewExporterID;
    @Wire("#windowCreateExporter  #txtNewDescription")
    private Textbox txtNewDescription;
    @Wire("#windowCreateExporter  #listDevice")
    private Listbox listDevice;
    @Wire("#windowCreateExporter  #bandDevice")
    private Bandbox bandDevice;

    @Wire("#windowCreateExporter  #btnSaveNewExporter")
    private Button btnSaveNewExporter;
    @Wire("#windowCreateExporter")
    private Window windowCreateExporter;

    private Pump selectedPump;
    private Exporter selectedExporter;

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }

    private void init(){

        Session session = Executions.getCurrent().getSession();
        selectedPump = (Pump) session.getAttribute("selectedPump");
        fillListExporters();
    }

    private void fillListExporters(){
        ListModel<Exporter> exporterListModel = new ListModelArray<Exporter>(PumpRestService.getAllExporters(selectedPump, true));
        listExporters.setModel(exporterListModel);
        listExporters.setItemRenderer((listitem, data, index) -> {
            final Exporter exporter = (Exporter) data;

            Listcell id = new Listcell();
            id.appendChild(new Label(exporter.getId()));
            listitem.appendChild(id);

            Listcell description = new Listcell();
            description.appendChild(new Label(exporter.getDescription()));
            listitem.appendChild(description);

          /*  Listcell destination = new Listcell();
            destination.appendChild(new Label(exporter.getAETitleTo()));
            listitem.appendChild(destination);*/
        });
    }

    @Listen("onClick=#btnAddExporter")
    public void setBtnAddExporter(){

        windowCreateExporter.setVisible(true);
        windowCreateExporter.setMode(Window.MODAL);

        ListModel<Device> deviceListModel = new ListModelArray<Device>(PumpRestService.getAllDevices(selectedPump, true));
        listDevice.setModel(deviceListModel);
        listDevice.setItemRenderer((listitem, data, index) -> {
            final Device device = (Device) data;

            Listcell deviceAETitle = new Listcell();
            deviceAETitle.appendChild(new Label(device.getAETitle()));
            listitem.appendChild(deviceAETitle);

        });
    }

    @Listen("onClick=window #btnSaveNewExporter")
    public void setBtnSaveNewExporter(){
        if(!isValideNewExporter()){
            Clients.showNotification("Please input all fields",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSaveNewExporter, "end_center", 2000);
            return;
        }

        Exporter exporter = new Exporter();
        exporter.setId(txtNewExporterID.getValue());
        exporter.setAETitleTo(bandDevice.getText());
        exporter.setDescription(txtNewDescription.getValue());
        exporter.setExportQueue("Export8");

        boolean status = LDAPService.addExporter(selectedPump, exporter);


        if(status){
            PumpRestService.reloadDevice(selectedPump);
            Clients.showNotification("New Exporter successfully added",
                    Clients.NOTIFICATION_TYPE_INFO,
                    btnSaveNewExporter, "end_center", 2000);

            closeWindowCreateExporter();
            init();
        }
        else{
                Clients.showNotification("Cant add current combination",
                        Clients.NOTIFICATION_TYPE_ERROR,
                        btnSaveNewExporter, "end_center", 2000);

        }

    }

    private boolean isValideNewExporter(){
        if(txtNewExporterID.getValue().equals("") || txtNewExporterID.getValue() == null
                || bandDevice.getText().equals("") || bandDevice.getText() == null
                || txtNewDescription.getValue().equals("") || txtNewDescription.getValue() == null
        ){
            return false;
        }
        return true;
    }

    @Listen("onClick=window #btnCancelNewExporter")
    public void btnClearNewExporter(){
        closeWindowCreateExporter();
    }

    public void closeWindowCreateExporter(){

        txtNewExporterID.setValue(null);
        bandDevice.setText(null);
        txtNewDescription.setValue(null);
        windowCreateExporter.setMode(Window.Mode.HIGHLIGHTED);
        windowCreateExporter.setVisible(false);
    }

    @Listen("onSelect=window #listDevice")
    public void selectDevice(){

        Device selectedDevice = (Device) listDevice.getListModel().getElementAt(listDevice.getSelectedIndex());
        bandDevice.setText(selectedDevice.getAETitle());
        bandDevice.close();

    }


}