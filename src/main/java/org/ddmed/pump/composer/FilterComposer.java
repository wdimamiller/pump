package org.ddmed.pump.composer;

import org.ddmed.pump.config.PumpConfig;
import org.ddmed.pump.model.Pump;
import org.ddmed.pump.model.Study;
import org.ddmed.pump.service.PumpRestService;
import org.ddmed.pump.service.PumpService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;


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
    Datebox btnSearch;
    @Wire
    Datebox btnClearFilter;
    @Wire
    Grid gridStudies;

    Pump selectedPump;

    private List<Study> studies;

    public void initComponents(){

        txtPatientID.setValue(null);
        txtPatientName.setValue(null);
        datePatientDOB.setValue(null);

        dateStadyFrom.setValue(new Date());
        dateStadyTo.setValue(new Date());

        bandFacilities.setText("All facilities");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(PumpConfig.class);

        selectedPump = context.getBean("currentPUMP", Pump.class);

        //List<String> modalities = PumpRestService.getModalities(selectedPump);

        //if(modalities!=null){

          //  for(int i = 0; i < modalities.size(); i++){
          //      listModalities.appendItem(modalities.get(i), modalities.get(i));
           // }
            /*ListModel<String> modalListModel = new ListModelArray<String>(modalities);
            listModalities.setModel(modalListModel);
            listModalities.setMultiple(true);
            listModalities.setCheckmark(true);*/

            /*listModalities.setItemRenderer((listitem, data, index) -> {
                final String mod = (String) data;

                Listcell cellName = new Listcell();
                cellName.appendChild(new Label(mod));
                listitem.appendChild(cellName);

            });*/
       // }

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

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String dateFrom = dateFormat.format(dateStadyFrom.getValue());
        String dateTo = dateFormat.format(dateStadyTo.getValue());

        studies = PumpRestService.getAllStudies(selectedPump, dateFrom, dateTo);
        fillStudyGrid();
    }

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        initComponents();
    }

}
