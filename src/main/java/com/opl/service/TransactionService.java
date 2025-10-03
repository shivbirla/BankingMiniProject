package com.opl.service;

import com.opl.dto.TransactionDTO;
import com.opl.dto.TransactionWithCustomerDTO;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public interface TransactionService {

    void withdrawAndDeposit(String accountNumber, BigDecimal amount,String type);

    String checkBalance(String AccountNum, Long userId);

    List<TransactionWithCustomerDTO> getAllTransactionHistory();
}
