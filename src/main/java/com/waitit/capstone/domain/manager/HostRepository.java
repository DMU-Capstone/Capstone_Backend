package com.waitit.capstone.domain.manager;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostRepository extends JpaRepository<Host,Long> {
    Optional<Host> findHostById(Long id);
    List<Host> findAllByIsActive(boolean isActive);
}
