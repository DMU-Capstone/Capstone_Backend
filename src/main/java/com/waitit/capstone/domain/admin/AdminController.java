package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.*; // DTO 임포트
import com.waitit.capstone.domain.image.AllImageResponse;
import com.waitit.capstone.domain.image.ImageService;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.global.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@RestController
@RequestMapping("/admin")
@Tag(name = "관리자 API", description = "관리자 기능 관련 API (관리자 권한 필요)")
public class AdminController {

    private final AdminService adminService;
    private final ImageService imageService;

    // ... (기존 회원 관련 API 생략)
    @Operation(summary = "모든 회원 조회", description = "관리자가 모든 회원을 페이지별로 조회합니다.")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AllUserRequest>> getAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        PageResponse<AllUserRequest> response = adminService.getAllUser(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 정보 수정", description = "관리자가 특정 회원의 정보를 수정합니다.")
    @PatchMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UpdatedRequest request){
        adminService.updateMember(request);
        Map<String,String> map = new HashMap<>();
        map.put("message","회원정보가 수정되었습니다.");
        return ResponseEntity.ok(map);
    }

    @Operation(summary = "회원 삭제", description = "관리자가 특정 회원을 삭제합니다.")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        adminService.deleteMember(id);
        Map<String,String> map = new HashMap<>();
        map.put("message","회원이 삭제되었습니다.");
        return ResponseEntity.ok(map);
    }


    @Operation(summary = "이벤트 배너 등록", description = "관리자가 이벤트 배너 이미지를 업로드합니다.")
    @PostMapping(value = "/event/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadEventBanner(@RequestParam("images") List<MultipartFile> eventImages){
        adminService.uploadEventImage(eventImages);
        return ResponseEntity.status(HttpStatus.CREATED).body("이미지 저장 완료");
    }

    @Operation(summary = "이벤트 배너 목록 조회", description = "모든 이벤트 배너를 페이지별로 조회합니다.")
    @GetMapping("/event")
    public ResponseEntity<PageResponse<AllImageResponse>> getAllImages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        PageResponse<AllImageResponse> images = imageService.getAllImage(pageable);
        return ResponseEntity.ok(images);
    }

    @Operation(summary = "이벤트 배너 삭제", description = "관리자가 특정 이벤트 배너를 삭제합니다.")
    @DeleteMapping("/event/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id){
        adminService.deleteImage(id);
        return ResponseEntity.status(HttpStatus.OK).body("이미지 삭제 완료");
    }

    @Operation(summary = "메인 배너 상태 변경 (ON/OFF)", description = "관리자가 메인 화면에 표시할 배너를 활성화(ON) 또는 비활성화(OFF)합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/banners/status")
    public ResponseEntity<Void> updateBannerStatus(@RequestBody UpdateBannerStatusRequest request) {
        adminService.updateBannerStatus(request);
        return ResponseEntity.ok().build();
    }

    // ... (기존 대기열 및 호스트 관련 API 생략)
    @Operation(summary = "활성 대기열 현황 조회", description = "관리자가 현재 활성화된 모든 대기열의 요약 정보를 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<?> getAllQueue(){
        List<HostSummaryDto> list = adminService.getActiveHostSummaries();
        return ResponseEntity.ok(list);
    }
    @Operation(summary = "호스트별 세부 대기열 내역 조회", description = "관리자가 특정 호스트의 세부 대기열 내역을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active/{hostId}")
    public ResponseEntity<?> findQueueByHostId(@PathVariable String hostId){
        List<QueueDto> list = adminService.getQueueDtoByHostId(hostId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "모든 호스트 조회", description = "관리자가 모든 호스트를 페이지별로 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hosts")
    public ResponseEntity<PageResponse<AllHostRequest>> getAllHost(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        PageResponse<AllHostRequest> response = adminService.getAllHost(pageable);
        return ResponseEntity.ok(response);
    }
}
