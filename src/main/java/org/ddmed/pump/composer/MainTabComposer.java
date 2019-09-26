package org.ddmed.pump.composer;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.domain.User;
import org.ddmed.pump.service.PumpRestService;
import org.ddmed.pump.service.PumpService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MainTabComposer extends SelectorComposer {

    @Wire
    private Bandbox bandCurrentPump;
    @Wire
    private Listbox listCurrentPump;
    @Wire
    private Tab tabHome;

    @WireVariable
    PumpService pumpService;

    private Pump selectedPump;

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        init();
    }


    @Listen("onClick=#tabHome")
    public void tabHomeClick(){

        fillListPump();

    }
    @Listen("onSelect=#listCurrentPump")
    public void fillBand(){

        selectedPump =(Pump) listCurrentPump.getListModel().getElementAt(listCurrentPump.getSelectedIndex());
        bandCurrentPump.setText(selectedPump.getName());
        bandCurrentPump.close();

        Session session = Executions.getCurrent().getSession();
        session.setAttribute("selectedPump", selectedPump );
    }

    private void fillListPump(){
        ListModel<Pump> pumpListModel = new ListModelArray<Pump>(pumpService.getAll());
        listCurrentPump.setModel(pumpListModel);
        listCurrentPump.setItemRenderer((listitem, data, index) -> {
            final Pump pump = (Pump) data;

            Listcell cellName = new Listcell();
            cellName.appendChild(new Label(pump.getName()));
            listitem.appendChild(cellName);

        });

        if(pumpListModel!=null && pumpListModel.getSize() > 0){

            listCurrentPump.setSelectedIndex(0);
            fillBand();
        }
    }
    public void init(){

        fillListPump();
    }


}
