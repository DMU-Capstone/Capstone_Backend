package com.waitit.capstone.domain.store.controller;

import com.waitit.capstone.domain.store.dto.StoreDetailResponse;
import com.waitit.capstone.domain.store.dto.StoreSummaryResponse;
import com.waitit.capstone.domain.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Tag(name = "매장 API", description = "매장 정보 조회 관련 API")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    @Operation(summary = "매장 목록 조회", description = "모든 매장의 요약된 정보를 목록으로 조회합니다.")
    public ResponseEntity<List<StoreSummaryResponse>> getStores() {
        List<StoreSummaryResponse> stores = storeService.getStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "매장 상세 조회", description = "특정 ID를 가진 매장의 상세 정보를 조회합니다.")
    public ResponseEntity<StoreDetailResponse> getStoreDetails(@PathVariable Long id) {
        StoreDetailResponse storeDetails = storeService.getStoreDetails(id);
        return ResponseEntity.ok(storeDetails);
    }
}
