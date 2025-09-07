package com.waitit.capstone.domain.manager.controller;

import com.waitit.capstone.domain.manager.service.HostService;
import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import com.waitit.capstone.domain.manager.dto.WaitingListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RequestMapping("/host")
@RestController
@Tag(name = "호스트 API", description = "호스트(가게) 관련 API")
public class HostController {
    private final HostService hostService;

    @Operation(summary = "호스트 등록", description = "신규 호스트(가게) 정보를 등록합니다.")
    @PostMapping
    public ResponseEntity<?> saveHost (
            @RequestPart("request") HostRequest request,
            @RequestPart(value = "hostImages", required = false) List<MultipartFile> hostImages
) throws IOException  {
        hostService.saveHost(request,hostImages);

        //추후 공통 응답바디로 변경
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "호스트 등록에 성공하였습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @Operation(summary = "호스트 정보 조회", description = "ID로 특정 호스트의 정보를 조회합니다.")
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<HostResponse> getHost(@PathVariable Long id){
            HostResponse response = hostService.getHost(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "세션 리스트 조회", description = "현재 활성화된 모든 세션 리스트를 조회합니다.")
    @GetMapping("/sessions")
    @ResponseBody
    public ResponseEntity<List<SessionListDto>> getSessionList(){
        List<SessionListDto> sessions = hostService.getAllSessions();
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @Operation(summary = "호스트별 대기열 조회", description = "특정 호스트의 현재 대기열 목록을 조회합니다.")
    @GetMapping("/waiting/{id}")
    public ResponseEntity<?> getWaitingList(@PathVariable Long id){
        List<WaitingListDto> list = hostService.getQueueListByHostId(id);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "인기 호스트 조회", description = "지정된 수만큼 인기있는 호스트 목록을 조회합니다.")
    @GetMapping("/trend")
    public ResponseEntity<List<SessionListDto>> getTrentHost(@RequestParam int count){
        List<SessionListDto> list = hostService.findTrendHost(count);
        return ResponseEntity.ok(list);
    }
}
