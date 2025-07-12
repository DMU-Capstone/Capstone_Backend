package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllHostRequest;
import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.admin.dto.HostSummaryDto;
import com.waitit.capstone.domain.admin.dto.MainBannerResponse;
import com.waitit.capstone.domain.admin.dto.UpdatedRequest;
import com.waitit.capstone.domain.image.AllImageResponse;
import com.waitit.capstone.domain.image.ImageService;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.global.util.PageResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ImageService imageService;
    //모든 회원 조회
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AllUserRequest>> getAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        PageResponse<AllUserRequest> response = adminService.getAllUser(pageable);
        return ResponseEntity.ok(response);
    }

    //회원 정보 수정
    @PatchMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UpdatedRequest request){
        adminService.updateMember(request);

        Map<String,String> map = new HashMap<>();
        map.put("message","회원정보가 수정되었습니다.");

        return ResponseEntity.ok(map);
    }

    //회원 삭제
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        adminService.deleteMember(id);

        Map<String,String> map = new HashMap<>();
        map.put("message","회원이 삭제되었습니다.");

        return ResponseEntity.ok(map);
    }

    //이벤트 배너 등록
    @PostMapping(value = "/event/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadEventBanner(@RequestParam("images") List<MultipartFile> eventImages){
        adminService.uploadEventImage(eventImages);
        return ResponseEntity.status(HttpStatus.CREATED).body("이미지 저장 완료");
    }

    //이벤트 배너 조회
    @GetMapping("/event")
    public ResponseEntity<PageResponse<AllImageResponse>> getAllImages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        PageResponse<AllImageResponse> images = imageService.getAllImage(pageable);
        return ResponseEntity.ok(images);
    }
    //이벤트 배너 삭제
    @DeleteMapping("/event/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id){
        adminService.deleteImage(id);
        return ResponseEntity.status(HttpStatus.OK).body("이미지 삭제 완료");
    }

    //메인 이벤트 배너 결정 기능
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/event/select")
    public ResponseEntity<?> selectEventBanner(@RequestParam Long imgId,@RequestParam int number){
        adminService.selectBanner(imgId,number);
        return ResponseEntity.status(HttpStatus.OK).body("이미지 이벤트 등록 완료");
    }

    //대기열 현황 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<?> getAllQueue(){
        List<HostSummaryDto> list = adminService.getActiveHostSummaries();
        return ResponseEntity.ok(list);
    }
    //세부 대기열 내역 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active/{hostId}")
    public ResponseEntity<?> findQueueByHostId(@PathVariable String hostId){
        List<QueueDto> list = adminService.getQueueDtoByHostId(hostId);
        return ResponseEntity.ok(list);
    }

    //모든 호스트 조회
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hosts")
    public ResponseEntity<PageResponse<AllHostRequest>> getAllHost(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        PageResponse<AllHostRequest> response = adminService.getAllHost(pageable);
        return ResponseEntity.ok(response);
    }
}
