package org.ddmed.pump.service;

import org.ddmed.pump.domain.Role;
import org.ddmed.pump.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void saveRole(List role){
        roleRepository.saveAll(role);
    }

    public List<Role> getAllRole(){
        return (List<Role>)roleRepository.findAll();
    }
}
