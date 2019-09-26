package org.ddmed.pump.composer;


import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.model.Study;
import org.ddmed.pump.service.PumpRestService;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class FilterComposer extends SelectorComposer {

    @Wire
    Textbox txtPatientID;
    @Wire
    Textbox txtPatientName;
    @Wire
    Datebox datePatientDOB;
    @Wire
    Bandbox bandFacilities;
    @Wire
    Bandbox bandStatuses;
    @Wire
    Listbox listFacilities;
    @Wire
    Bandbox bandModalities;
    @Wire
    Listbox listModalities;
    @Wire
    Listbox listStatuses;
    @Wire
    Combobox listTypeDate;
    @Wire
    Datebox dateStadyFrom;
    @Wire
    Datebox dateStadyTo;
    @Wire
    Button btnSearch;
    @Wire
    Button btnClearFilter;
    @Wire
    Grid gridStudies;

    Pump selectedPump;

    private List<Study> studies;

    public boolean checkServer(){
        Session session = Executions.getCurrent().getSession();
        selectedPump = (Pump) session.getAttribute("selectedPump");

        if(selectedPump == null){
            System.out.println("IT IS NULL");
            return false;
        }
        else{

            RestTemplate restTemplate = new RestTemplate();
            String uri = selectedPump.getRestBase() + "/ctrl/status";
            System.out.println(uri);
            String result;
            try {
                String httpResult = restTemplate.getForObject(uri,
                        String.class);
                System.out.println(httpResult);
            } catch (HttpStatusCodeException e) {
                System.out.println("HTTP ERROR");
                return false;
            } catch (RuntimeException e) {
                System.out.println("RUNTIME ERROR");
                return false;
            }

        }
        return true;
    }

    public void fillFilterComponents(){
        if(!checkServer()){
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

        if(!checkServer()){
            Clients.showNotification("Cant connect to server, " +
                            "check is DCM4CHEE is running or " +
                            "if creadentials are correct",
                    Clients.NOTIFICATION_TYPE_ERROR,
                    btnSearch, "end_center", 100);
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
    }

}
