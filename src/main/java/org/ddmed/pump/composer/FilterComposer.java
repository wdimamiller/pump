package org.ddmed.pump.composer;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.model.Study;
import org.ddmed.pump.service.PumpRestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
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

    private Pump selectedPump;

    private List<Study> studies;

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

        clearFilters();
        fillFilterComponents();
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


        ListModelList<Study> gridListModel = new ListModelList<>(studies);
        gridStudies.setModel(gridListModel);

        auxHeader.setLabel("Studies    find : " + studies.size() );
        gridStudies.setRowRenderer((RowRenderer) (row, data, index) -> {

            final Study study = (Study) data;

            row.appendChild(new Label(study.getPatientName()));
            row.appendChild(new Label(study.getPatientDOB()));
            row.appendChild(new Label(study.getPatientID()));
            row.appendChild(new Label(study.getPatientGender()));

            row.appendChild(new Label(study.getInstitutionName()));
            row.appendChild(new Label(study.getModality()));
            row.appendChild(new Label(study.getStudyDate()));
            row.appendChild(new Label(String.valueOf(study.getCountSeries())));

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
