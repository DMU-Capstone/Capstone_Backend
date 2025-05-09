package com.waitit.capstone.domain.manager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostRepository extends JpaRepository<Host,Long> {
    Host findHostById(Long id);
}
