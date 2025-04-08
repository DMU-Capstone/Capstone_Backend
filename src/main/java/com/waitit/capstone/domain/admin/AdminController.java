package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.admin.dto.UpdatedRequest;
import com.waitit.capstone.global.util.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    //모든 회원 조회
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<AllUserRequest>> getAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        PageResponse<AllUserRequest> response = adminService.getAllUser(pageable);
        return ResponseEntity.ok(response);
    }

    //회원 정보 수정
    @PatchMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UpdatedRequest request){

        adminService.updateMember(request);
        return ResponseEntity.ok("회원정보가 수정되었습니다.");
    }

    //회원 삭제
    @DeleteMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> DeleteUser(@RequestParam Long id){
        adminService.deleteMember(id);
        return ResponseEntity.ok("회원이 삭제되었습니다.");
    }

    //이벤트 배너 등록

    //대기열 현황 조회
}
