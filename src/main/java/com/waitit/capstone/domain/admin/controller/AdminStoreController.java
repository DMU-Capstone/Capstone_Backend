package com.waitit.capstone.domain.admin.controller;

import com.waitit.capstone.domain.admin.dto.UpdateStoreRequest;
import com.waitit.capstone.domain.store.dto.StoreDetailResponse;
import com.waitit.capstone.domain.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
@Tag(name = "관리자 매장 API", description = "관리자 매장 정보 관리 관련 API")
public class AdminStoreController {

    private final StoreService storeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "매장 요약 정보 수정", description = "관리자가 매장의 이름(title)과 대표 이미지(imgUrl)를 수정합니다.")
    public ResponseEntity<Void> updateStoreSummary(@RequestBody UpdateStoreRequest request) {
        storeService.updateStoreSummary(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")//
    @Operation(summary = "매장 상세 조회 (관리자용)", description = "관리자가 특정 ID를 가진 매장의 상세 정보를 조회합니다.")
    public ResponseEntity<StoreDetailResponse> getStoreDetails(@PathVariable Long id) {
        StoreDetailResponse storeDetails = storeService.getStoreDetails(id);
        return ResponseEntity.ok(storeDetails);
    }
}
