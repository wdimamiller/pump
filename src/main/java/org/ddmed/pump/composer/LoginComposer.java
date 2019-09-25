package org.ddmed.pump.composer;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class LoginComposer extends SelectorComposer<Window> {

    @Wire
    Window windowLogin;
    @Wire
    Button btnSubmit;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        checkError();
    }

    public void checkError(){
        if (Executions.getCurrent().getParameter("error") != null) {
                Clients.showNotification(Labels.getLabel("login.labelErrorNotification"),"error", windowLogin,"end_center",3000);
        }
    }

    @Listen("onClose=#windowLogin")
    public void closeWindow(){
        Executions.sendRedirect("/index.zul");
    }
}
