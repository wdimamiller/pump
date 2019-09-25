package org.ddmed.pump.composer;

import org.ddmed.pump.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Toolbarbutton;

public class UserComposer extends SelectorComposer {

    @Wire
    private Toolbarbutton userAccount;
    private String userName;


    @AfterCompose
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        init();
    }
    public void init(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            User user = (User) principal;
            userName = user.getFirstName() + " " + user.getLastName();
        } else {
            userName = "UNKNOWN";
        }

        userAccount.setLabel(userName);
    }

}
