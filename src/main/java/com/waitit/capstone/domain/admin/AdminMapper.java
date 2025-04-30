package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.client.member.Entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AllUserRequest toAllUserRequest(Member member);
}
