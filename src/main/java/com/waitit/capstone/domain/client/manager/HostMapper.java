package com.waitit.capstone.domain.client.manager;


import com.waitit.capstone.domain.client.manager.dto.HostRequest;
import com.waitit.capstone.domain.client.manager.dto.HostResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HostMapper {

    HostResponse hostToDto(Host host);

    Host toEntity(HostRequest request);
}
