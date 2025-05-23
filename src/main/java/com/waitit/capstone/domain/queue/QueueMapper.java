package com.waitit.capstone.domain.queue;

import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.dto.QueueRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueueMapper {
    QueueDto requestToDto(QueueRequest request);
}
