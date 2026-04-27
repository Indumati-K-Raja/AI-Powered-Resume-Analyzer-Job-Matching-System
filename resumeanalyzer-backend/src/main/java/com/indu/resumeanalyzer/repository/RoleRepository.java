package com.indu.resumeanalyzer.repository;

import com.indu.resumeanalyzer.entity.Role;
import com.indu.resumeanalyzer.entity.Role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
