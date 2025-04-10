package com.waitit.capstone.domain.client.manager;

import com.waitit.capstone.domain.client.manager.dto.HostRequest;
import com.waitit.capstone.domain.client.manager.dto.HostResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class HostService {
    private final HostRepository hostRepository;

    public boolean hostExist(Long id){
        return true;
    }
    //호스트 정보 저장
    public void saveHost(HostRequest request){
        Host host = request.toEntity();
        hostRepository.save(host);
        //레디스에 세션 올리기 필요
    }

    //요청받은 아이디로 db에서 호스트 조회
    public HostResponse getHost(Long id){
        Host host = hostRepository.findHostById(id);
        return HostResponse.from(host);
    }


}
