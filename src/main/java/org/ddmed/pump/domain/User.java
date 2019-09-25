package org.ddmed.pump.domain;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name="USER")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private long id;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "ACCOUNT_NON_EXPIRED")
    private boolean accountNonExpired;
    @Column(name = "ACCOUNT_NON_LOCKED")
    private boolean accountNonLocked;
    @Column(name = "CREDENTIAL_NON_EXPIRED")
    private boolean credentialsNonExpired;
    @Column(name = "ENABLED")
    private boolean enabled;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;


    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "USER_ROLE", joinColumns = {@JoinColumn(name = "USER_ID")}, inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public void setPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPasword = passwordEncoder.encode(password);
        this.password = hashedPasword;
    }
}
