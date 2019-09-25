package org.ddmed.pump.composer;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;


public class IndexComposer extends SelectorComposer {

    @Wire
    Combobox comboTheme;

    @Listen("onSelect=#comboTheme")
    public void changeTheme(){
        String theme = comboTheme.getSelectedItem().getLabel();
        Library.setProperty("org.zkoss.theme.preferred", theme);
        Executions.sendRedirect("");
    }

    @Listen("onClick=#btnLogin")
    public void openLoginWindow(){
        Window window = (Window)Executions.createComponents("/login.zul", null, null);
        window.doModal();
    }

    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
    }

}
