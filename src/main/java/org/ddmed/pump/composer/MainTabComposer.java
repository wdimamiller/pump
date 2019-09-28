package org.ddmed.pump.composer;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.service.PumpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MainTabComposer extends SelectorComposer {

    private static final Logger log = LoggerFactory.getLogger(MainTabComposer.class);

    @Wire
    private Textbox txtCurrentPump;

    @WireVariable
    private PumpService pumpService;

    private Pump selectedPump;

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        Session session = Executions.getCurrent().getSession();
        session.setAttribute("mainTab", this);
        init();
    }

    public void init(){
        checkPump();
    }

    public void  checkPump(){

        Session session = Executions.getCurrent().getSession();
        selectedPump = (Pump) session.getAttribute("selectedPump");

        if(selectedPump == null){

            log.info("On start not selected pump");
            log.info("Trying to select default from database");

            Pump pump = pumpService.getDefault();

            if (pump != null) {
                session.setAttribute("selectedPump", pump );
                txtCurrentPump.setValue(pump.getName());
            }
            else{
                log.info("Not find default pump in database");
                txtCurrentPump.setValue("UNKNOWN");
            }
        }
        else{
            txtCurrentPump.setValue(selectedPump.getName());
        }

    }
}
