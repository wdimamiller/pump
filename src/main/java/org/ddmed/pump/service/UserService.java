package org.ddmed.pump.service;

import com.google.common.collect.ImmutableList;
import org.ddmed.pump.domain.Role;
import org.ddmed.pump.domain.User;
import org.ddmed.pump.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;




@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        User user = userRepository.findByUsername(username);

        return user;
    }

    public void createDefaultUser(){
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
