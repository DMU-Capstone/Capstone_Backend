package com.waitit.capstone.domain.image.repository;

import com.waitit.capstone.domain.image.entity.HostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostImageRepository extends JpaRepository<HostImage,Long> {
    // 이미지 경로로 HostImage를 찾는 메소드 추가
    Optional<HostImage> findByImagePath(String imagePath);
}
