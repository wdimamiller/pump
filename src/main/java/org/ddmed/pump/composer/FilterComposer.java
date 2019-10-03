package org.ddmed.pump.composer;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.model.Device;
import org.ddmed.pump.model.Study;
import org.ddmed.pump.service.PumpRestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class FilterComposer extends SelectorComposer {

    private static final Logger log = LoggerFactory.getLogger(FilterComposer.class);

    @Wire
    private Textbox txtPatientID;
    @Wire
    private Textbox txtPatientName;
    @Wire
    private Datebox datePatientDOB;
    @Wire
    private Bandbox bandFacilities;
    @Wire
    private Bandbox bandStatuses;
    @Wire
    private Listbox listFacilities;
    @Wire
    private Bandbox bandModalities;
    @Wire
    private Listbox listModalities;
    @Wire
    private Listbox listStatuses;
    @Wire
    private Combobox listTypeDate;
    @Wire
    private Datebox dateStadyFrom;
    @Wire
    private Datebox dateStadyTo;
    @Wire
    private Button btnSearch;
    @Wire
    private Button btnClearFilter;
    @Wire
    private Auxheader auxHeader;
    @Wire
    private Grid gridStudies;

    @Wire
    private Window windowSendStudy;
    @Wire
    private Button btnOpenSendWindow;
    @Wire("#windowSendStudy  #btnSendStudies")
    private Button btnSendStudies;
    @Wire("#windowSendStudy  #listDevices")
    private Listbox listDevices;
    @Wire("#windowSendStudy  #bandDevices")
    private Bandbox bandDevices;
    @Wire("#windowSendStudy  #lblCountSelected")
    private Label lblCountSelected;


    private ListModelList<Study> gridListModel;

    private Pump selectedPump;

    private List<Study> studies;

    private List<Study> selectedStudies;

    public void fillFilterComponents(){
        if(!PumpRestService.isWorked(selectedPump)){
            return;
        }
        //Modalities
        List<String>  modalities = PumpRestService.getModalities(selectedPump);
        if(modalities!=null) {

            for (int i = 0; i < modalities.size(); i++) {
                listModalities.appendItem(modalities.get(i), modalities.get(i));
            }
        }
    }
    public void init(){

        Session session = Executions.getCurrent().getSession();
        selectedPump = (Pump) session.getAttribute("selectedPump");

        selectedStudies = new ArrayList<Study>();
        clearFilters();
        fillFilterComponents();
    }

    @Listen("onClick=#btnOpenSendWindow")
    public void openSendWindow(){
        lblCountSelected.setValue("Studies for send: " + selectedStudies.size());
        windowSendStudy.setVisible(true);
        windowSendStudy.setMode(Window.MODAL);
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

        });
    }


    @Listen("onClick=window #btnCloseWindow")
    public void closeSendWindow(){

        bandDevices.setText(null);
        lblCountSelected.setValue(null);
        windowSendStudy.setVisible(false);
    }

    @Listen("onSelect=window #listDevices")
    public void selectDevice(){

        Device selectedDevice = (Device) listDevices.getListModel().getElementAt(listDevices.getSelectedIndex());
        bandDevices.setText(selectedDevice.getAETitle());
        bandDevices.close();

    }

    @Listen("onClick=window #btnSendStudies")
    public void btnSendStudies(){

        int index = listDevices.getSelectedIndex();
        if(index < 0){
            Clients.showNotification("Please, choose AETitle",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSendStudies, "end_center", 100);
            return;
        }
        Device selectedDevice = (Device) listDevices.getListModel().getElementAt(listDevices.getSelectedIndex());
        if (selectedDevice == null){
            Clients.showNotification("Please, choose AETitle",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSendStudies, "end_center", 100);
            return;
        }

        PumpRestService.exportStudies(selectedPump, selectedStudies, selectedDevice);

    }



    public void clearFilters(){


        txtPatientID.setValue(null);
        txtPatientName.setValue(null);
        datePatientDOB.setValue(null);


        bandFacilities.setText("All facilities");
        listFacilities.selectAll();

        bandStatuses.setText("All statuses");
        listStatuses.selectAll();

        dateStadyFrom.setValue(new Date());
        dateStadyTo.setValue(new Date());

        bandModalities.setText("All modalities");

        listTypeDate.setSelectedIndex(0);

        listModalities.selectAll();

    }


    private void fillStudyGrid(){

        selectedStudies = new ArrayList<Study>();
        gridListModel = new ListModelList<>(studies);
        gridStudies.setModel(gridListModel);

        auxHeader.setLabel("Studies    find : " + studies.size() );
        gridStudies.setRowRenderer((RowRenderer) (row, data, index) -> {

            final Study study = (Study) data;

            Button btnOviyam = new Button();
            btnOviyam.setIconSclass("z-icon-search");
            row.appendChild(btnOviyam);

            row.appendChild(new Label(study.getPatientName()));
            row.appendChild(new Label(study.getPatientDOB()));
            row.appendChild(new Label(study.getPatientID()));
            row.appendChild(new Label(study.getPatientGender()));

            row.appendChild(new Label(study.getInstitutionName()));
            row.appendChild(new Label(study.getModality()));
            row.appendChild(new Label(study.getStudyDate()));
            row.appendChild(new Label(String.valueOf(study.getCountSeries())));
            Checkbox checkboxSelect = new Checkbox();
            checkboxSelect.addEventListener("onCheck", new EventListener<Event>() {

                @Override
                public void onEvent(Event event) throws Exception {

                        Study currentClickedStudy = gridListModel.get(((Row) event.getTarget().getParent()).getIndex());
                        if (((CheckEvent) event).isChecked()) {
                            selectedStudies.add(currentClickedStudy);
                        } else {
                            selectedStudies.remove(currentClickedStudy);
                        }

                    }

            });
            row.appendChild(checkboxSelect);

        });


    }
    @Listen("onClick=#btnSearch")
    public void clickBtnSearch(){

        if(!PumpRestService.isWorked(selectedPump)){
            Clients.showNotification("Cant connect to server, " +
                            "check is DCM4CHEE is running or " +
                            "if creadentials are correct",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSearch, "end_center", 2000);
        }
        else{
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

            String dateFrom = dateFormat.format(dateStadyFrom.getValue());
            String dateTo = dateFormat.format(dateStadyTo.getValue());

            Date patientDOB = datePatientDOB.getValue();
            String strPAtientDOB = null;
            if(patientDOB!=null){
                strPAtientDOB = dateFormat.format(datePatientDOB.getValue());
            }

            String patientName = txtPatientName.getValue();
            String patientID = txtPatientID.getValue();

            List<String> modalities = new ArrayList<String>();
            if(listModalities.getSelectedItems() != null){
                Set<Listitem> items = listModalities.getSelectedItems();

                items.forEach(listitem -> {
                    modalities.add(listitem.getLabel());
                    System.out.println(listitem.getLabel());
                });

            }

            studies = PumpRestService.getAllStudies(selectedPump, dateFrom, dateTo, patientName, strPAtientDOB, patientID, modalities);
            fillStudyGrid();
        }

    }

    @Listen("onClick=#btnClearFilter")
    public void clickBtnClearFilter(){
        clearFilters();
    }

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
        Session session = Executions.getCurrent().getSession();
        session.setAttribute("homeTab", this);
    }

}
