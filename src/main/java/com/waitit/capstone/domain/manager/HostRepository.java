package com.waitit.capstone.domain.manager;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostRepository extends JpaRepository<Host,Long> {
    Optional<Host> findHostById(Long id);
    List<Host> findAllByIsActive(boolean isActive);

    /**
     * [추가] 가장 최근에 생성된 Host를 count만큼 조회합니다.
     */
    List<Host> findTopByOrderByIdDesc(Pageable pageable);
}
