package com.opl.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@NonNull
public class CustomerRequest {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;
    private  String role;
    private List<CustomerAddressDTO> addresses;
    private  AccountDTO accountDTO;
}
