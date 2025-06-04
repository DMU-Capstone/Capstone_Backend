package com.waitit.capstone.domain.manager;


import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.WaitingListDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HostMapper {

    HostResponse hostToDto(Host host);

    Host toEntity(HostRequest request);

    List<WaitingListDto> queueToWaiting(List<QueueDto> list);
}
