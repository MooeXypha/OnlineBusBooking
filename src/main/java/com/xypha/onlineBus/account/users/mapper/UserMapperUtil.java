package com.xypha.onlineBus.account.users.mapper;

import com.xypha.onlineBus.account.role.Role;
import com.xypha.onlineBus.account.users.dto.UserRequest;
import com.xypha.onlineBus.account.users.dto.UserResponse;
import com.xypha.onlineBus.account.users.entity.User;

public class UserMapperUtil {
    public static User toEntity(UserRequest request){
        User u = new User();
        u.setUsername(request.getUsername());
        u.setPassword(request.getPassword());
        u.setGmail(request.getGmail());
        u.setPhoneNumber(request.getPhoneNumber());
        u.setNrc(request.getNrc());
        u.setGender(request.getGender());
        u.setDob(request.getDob());
        u.setCitizenship(request.getCitizenship());

        u.setRole(request.getRole() != null ? request.getRole() : Role.USER);
        return u;
    }

    public static UserResponse toDTO(User u){
        UserResponse response = new UserResponse();
        response.setId(u.getId());
        response.setUsername(u.getUsername());
        response.setGmail(u.getGmail());
        response.setPhoneNumber(u.getPhoneNumber());
        response.setPassword(u.getPassword());
        response.setNrc(u.getNrc());
        response.setGender(u.getGender());
        response.setDob(u.getDob() != null ? u.getDob().toString() : null);
        response.setCitizenship(u.getCitizenship());

        response.setRole(u.getRole());


        return response;
}
}
