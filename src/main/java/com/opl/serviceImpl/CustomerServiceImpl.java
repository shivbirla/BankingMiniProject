
package com.opl.serviceImpl;

import com.opl.authClient.AuthServiceClient;
import com.opl.domain.Account;
import com.opl.domain.Customer;
import com.opl.domain.CustomerAddress;
import com.opl.dto.CustomerAddressDTO;
import com.opl.dto.CustomerRequest;
import com.opl.repository.AccountRepository;
import com.opl.repository.CustomerAddressRepository;
import com.opl.repository.CustomerRepository;
import com.opl.request.AuthRequest;
import com.opl.request.AuthResponse;
import com.opl.request.SecurityUserDTO;
import com.opl.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerAddressRepository addressRepository;
    @Autowired
    private AuthServiceClient authServiceClient;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @Override
    public Customer saveOrUpdateCustomer(CustomerRequest request) {
        Customer customer;
        if (request.getId() != null) {
            customer = customerRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getId()));
        } else {
            customer = new Customer();

            if (customerRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new RuntimeException("Username already exists.");
            }
            if (request.getPassword() == null) {
                throw new IllegalArgumentException("Password is required for a new user.");
            }

            SecurityUserDTO securityRequest = new SecurityUserDTO();
            securityRequest.setUsername(request.getUsername());
            securityRequest.setPassword(request.getPassword());
            securityRequest.setRole(request.getRole()!=null ? request.getRole() : "CUSTOMER");

            ResponseEntity<SecurityUserDTO> authResponse =  authServiceClient.registerUserInternal(securityRequest);

            if (!authResponse.getStatusCode().is2xxSuccessful() || authResponse.getBody() == null) {
                throw new RuntimeException("Failed to register security user in Auth Service.");
            }
            customer.setUserId(authResponse.getBody().getId());
            customer.setUsername(request.getUsername());
        }
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());

        Customer savedCustomer = customerRepository.save(customer);

        if(request.getAccountDTO()!=null && request.getAccountDTO().getBalance()!=null){
        createInitialAccount(savedCustomer.getId(), request.getAccountDTO().getBalance());
        }

        if (request.getAddresses() != null) {
            updateCustomerAddresses(savedCustomer, request.getAddresses());
        }
        return savedCustomer;
    }

    @Override
    public void updateCustomerAddresses(Customer customer, List<CustomerAddressDTO> addressDTOs) {
        if (customer.getAddresses() != null && !customer.getAddresses().isEmpty()) {
            addressRepository.deleteAll(customer.getAddresses());
        }

        List<CustomerAddress> newAddresses = addressDTOs.stream()
                .map(dto -> {
                    CustomerAddress address = new CustomerAddress();
                    address.setCustomer(customer);
                    address.setAddressLine1(dto.getAddressLine1());
                    address.setAddressLine2(dto.getAddressLine2());
                    address.setCity(dto.getCity());
                    address.setState(dto.getState());
                    address.setPostalCode(dto.getPostalCode());
                    address.setCountry(dto.getCountry());
                    address.setAddressType(dto.getAddressType());
                    return address;
                })
                .collect(Collectors.toList());
        addressRepository.saveAll(newAddresses);
        customer.setAddresses(newAddresses);
    }

    private String generateUniqueAccountNumber() {
        long min = 10000000000000L;
        long max = 99999999999999L;
        String accountNumber = String.valueOf(ThreadLocalRandom.current().nextLong(min, max + 1));
        return accountNumber;
    }

    private void createInitialAccount(Long customerUserId, BigDecimal balance) {
        Account account = new Account();
        account.setUserId(customerUserId);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType("SAVINGS");
        account.setBalance(balance);
        accountRepository.save(account);
    }

    @Override
    public ResponseEntity<AuthResponse> getUserDetails(AuthRequest request){
        AuthResponse authResponse = new AuthResponse();
        Optional<Customer> customer = customerRepository.findByUsername(request.getUsername());
        if(customer.isEmpty()){
            authResponse.setMsg("Error: User Not Registered. Please register first.");
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<AuthResponse> JWTResponse =  authServiceClient.generateJWT(request);
        BeanUtils.copyProperties(JWTResponse.getBody(), authResponse);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}