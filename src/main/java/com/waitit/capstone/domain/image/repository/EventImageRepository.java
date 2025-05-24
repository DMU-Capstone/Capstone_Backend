package com.waitit.capstone.domain.image.repository;

import com.waitit.capstone.domain.image.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventImageRepository extends JpaRepository<EventImage,Long> {
}
