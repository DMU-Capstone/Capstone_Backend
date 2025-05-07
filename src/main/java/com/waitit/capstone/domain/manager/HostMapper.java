package com.waitit.capstone.domain.manager;


import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HostMapper {

    HostResponse hostToDto(Host host);

    Host toEntity(HostRequest request);
}
