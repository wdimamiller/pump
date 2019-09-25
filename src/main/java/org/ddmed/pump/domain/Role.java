package org.ddmed.pump.domain;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
@Data
@Entity
@Table(name = "ROLE")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue
    @Column(name="ROLE_ID")
    private long id;
    @Column(name="ROLE_NAME")
    private String name;


    @Override
    public String getAuthority() {
        return name;
    }
}
