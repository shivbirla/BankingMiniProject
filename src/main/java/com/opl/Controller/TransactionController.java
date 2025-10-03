package com.opl.Controller;

import com.opl.dto.TransactionWithCustomerDTO;
import com.opl.dto.WithdrawaAndDepositlRequest;
import com.opl.request.AuthResponse;
import com.opl.service.TransactionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("withdrawAndDeposit")
    public ResponseEntity<String> makeWithdrawal(@RequestBody WithdrawaAndDepositlRequest request, @RequestHeader(name = "Authorization") String token) {
        try {
            transactionService.withdrawAndDeposit(request.accountNumber(), request.amount(), request.type());
            return ResponseEntity.ok("Withdrawal successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Withdrawal failed: " + e.getMessage());
        }
    }

    @GetMapping("check_balance/{accNum}")
    public ResponseEntity<String> checkBalance(@PathVariable("accNum") String accNum, @RequestHeader(name = "Authorization") String token ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication == null) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        AuthResponse authResponse = new AuthResponse();
        BeanUtils.copyProperties(principal, authResponse);
        try {
            return ResponseEntity.ok(transactionService.checkBalance(accNum ,authResponse.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("check Balance failed: " + e.getMessage());
        }
    }

    @GetMapping("getAllTransaction")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAllTransaction(@RequestHeader(name = "Authorization") String token ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new ResponseEntity<>("User not authenticated.", HttpStatus.UNAUTHORIZED);
        }

        try {
            List<TransactionWithCustomerDTO> allTransactionHistory = transactionService.getAllTransactionHistory();
            return ResponseEntity.ok(allTransactionHistory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("check Balance failed: " + e.getMessage());
        }
    }
}
