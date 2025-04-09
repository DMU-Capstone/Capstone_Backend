package com.waitit.capstone.domain.client.manager;

import com.waitit.capstone.domain.client.manager.dto.HostRequest;
import com.waitit.capstone.domain.client.manager.dto.HostResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@RequestMapping("/host")
public class HostController {
    private final HostService hostService;

    @PostMapping
    public ResponseEntity<?> saveHost(@RequestBody HostRequest request){

        hostService.saveHost(request);

        //추후 공통 응답바디로 변경
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "호스트 등록에 성공하였습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping
    public ResponseEntity<HostResponse> getHost(@RequestParam Long id){
            HostResponse response = hostService.getHost(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
