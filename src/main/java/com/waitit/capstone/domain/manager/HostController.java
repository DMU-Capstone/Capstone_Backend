package com.waitit.capstone.domain.manager;

import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RequestMapping("/host")
@RestController
public class HostController {
    private final HostService hostService;

    @PostMapping
    public ResponseEntity<?> saveHost(@RequestBody HostRequest request,List<MultipartFile> hostImages)
            throws IOException {

        hostService.saveHost(request,hostImages);

        //추후 공통 응답바디로 변경
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "호스트 등록에 성공하였습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<HostResponse> getHost(@PathVariable Long id){
            HostResponse response = hostService.getHost(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/sessions")
    @ResponseBody
    public ResponseEntity<List<SessionListDto>> getSessionList(){
        List<SessionListDto> sessions = hostService.getAllSessions();
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }
}
