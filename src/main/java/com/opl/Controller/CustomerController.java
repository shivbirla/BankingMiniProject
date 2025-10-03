package com.opl.Controller;
import com.opl.domain.Customer;
import com.opl.dto.CustomerRequest;
import com.opl.request.AuthRequest;
import com.opl.request.AuthResponse;
import com.opl.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/user_registration")
    public ResponseEntity<Customer> saveOrUpdateUser(@RequestBody CustomerRequest request) {
        try {
            Customer savedCustomer = customerService.saveOrUpdateCustomer(request);

            if (request.getId() == null) {
                // New user created
                return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
            } else {
                // Existing user updated
                return new ResponseEntity<>(savedCustomer, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/user_login")
    public ResponseEntity<AuthResponse> userLogin(@RequestBody AuthRequest request) {
        try {
            return customerService.getUserDetails(request);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
