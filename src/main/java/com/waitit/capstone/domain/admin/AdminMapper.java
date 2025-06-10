package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllHostRequest;
import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.member.Entity.Member;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AllUserRequest toAllUserRequest(Member member);
    @Mapping(target = "isActive", constant = "true")
    AllHostRequest toAllHostRequest(Host host);
}
