package com.waitit.capstone.domain.image;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    AllImageResponse toAllImageResponse(EventImage eventImage);
}
