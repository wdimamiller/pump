package org.ddmed.pump.service;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.ddmed.pump.domain.Role;
import org.ddmed.pump.domain.User;
import org.ddmed.pump.repository.UserRepository;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = new User();
        user = userRepository.findByUsername(username);
       /*// user.setAuthorities(new ArrayList<Role>(new Role(1,"USER")));
        user.setUsername("user");
        user.setPassword("password");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);*/

        /*org.springframework.security.core.userdetails.User.UserBuilder builder = null;
        if (user != null) {

            builder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
            builder.password(new BCryptPasswordEncoder().encode(user.getPassword()));
            builder.roles("USER");
        } else {
            throw new UsernameNotFoundException("User not found.");
        }

        return builder.build();*/
        return user;
    }

    public void defaultUser(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setUsername("admin");
        user.setPassword("paris");
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setRoles(ImmutableList.of(role));

        userRepository.save(user);
    }

}
