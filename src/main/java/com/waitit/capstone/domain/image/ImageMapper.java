package com.waitit.capstone.domain.image;

import com.waitit.capstone.domain.image.entity.EventImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    AllImageResponse toAllImageResponse(EventImage eventImage);
}
