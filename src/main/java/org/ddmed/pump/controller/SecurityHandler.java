package org.ddmed.pump.controller;

/*import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;*/

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class SecurityHandler /*implements AuthenticationSuccessHandler */{

   /* @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {
            httpServletResponse.sendRedirect("zul/admin/admin.zul");
        }
        if (roles.contains("ROLE_DOCTOR")) {
            httpServletResponse.sendRedirect("zul/doctor/doctor.zul");
        }
        if (roles.contains("ROLE_MANAGER")) {
            httpServletResponse.sendRedirect("zul/manager/manager.zul");
        }
        if (roles.contains("ROLE_NURCE")) {
            httpServletResponse.sendRedirect("zul/nurce/nurce.zul");
        }
        if (roles.contains("ROLE_LAB_ASSISTANT")) {
            httpServletResponse.sendRedirect("zul/labassistant/labassistant.zul");
        }
        if (roles.contains("ROLE_ACCOUNTANT")) {
            httpServletResponse.sendRedirect("zul/accountant/accountant.zul");
        }
        if (roles.contains("ROLE_RES_PERSON")) {
            httpServletResponse.sendRedirect("zul/resperson/resperson.zul");
        }

    }*/
}