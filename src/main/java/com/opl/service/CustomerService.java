package com.opl.service;

import com.opl.domain.Customer;
import com.opl.dto.CustomerAddressDTO;
import com.opl.dto.CustomerRequest;
import com.opl.request.AuthRequest;
import com.opl.request.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public interface CustomerService {
    @Transactional
    Customer saveOrUpdateCustomer(CustomerRequest request);

    void updateCustomerAddresses(Customer customer, List<CustomerAddressDTO> addressDTOs);

    ResponseEntity<AuthResponse> getUserDetails(AuthRequest request);
}
