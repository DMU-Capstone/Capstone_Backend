package com.waitit.capstone.domain.notice.repository;

import com.waitit.capstone.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
