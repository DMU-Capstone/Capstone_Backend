package com.waitit.capstone.domain.image.repository;

import com.waitit.capstone.domain.image.entity.HostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostImageRepository extends JpaRepository<HostImage,Long> {
}
