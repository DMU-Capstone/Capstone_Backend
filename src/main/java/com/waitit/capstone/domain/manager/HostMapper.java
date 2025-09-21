package com.waitit.capstone.domain.manager;

import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.WaitingListDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = com.waitit.capstone.domain.image.entity.HostImage.class)
public interface HostMapper {

    @Mapping(target = "imgUrl", expression = "java(host.getImages().stream().filter(HostImage::isRepresentative).findFirst().map(HostImage::getImagePath).orElse(host.getImages().isEmpty() ? null : host.getImages().get(0).getImagePath()))")
    HostResponse hostToDto(Host host);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "station", ignore = true)
    @Mapping(target = "distance", ignore = true)
    Host toEntity(HostRequest request);

    List<WaitingListDto> queueToWaiting(List<QueueDto> list);
}
