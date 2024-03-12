package com.example.bankservice.dto.request;

import com.example.bankservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String email;
    private Role role = Role.ROLE_USER;
    private String phoneNumber;
    private String password;
    private String status;
}
