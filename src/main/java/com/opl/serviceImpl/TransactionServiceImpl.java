package com.opl.serviceImpl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opl.domain.Account;
import com.opl.domain.Customer;
import com.opl.domain.Transaction;
import com.opl.dto.CustomerAddressDTO;
import com.opl.dto.TransactionWithCustomerDTO;
import com.opl.repository.AccountRepository;
import com.opl.repository.CustomerRepository;
import com.opl.repository.TransactionRepository;
import com.opl.service.TransactionService;
import oracle.sql.TIMESTAMPTZ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

        @Transactional(isolation = Isolation.SERIALIZABLE)
        @Override
        public void withdrawAndDeposit(String accountNumber, BigDecimal amount, String type) {
            Transaction withdrawal = new Transaction();
            Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                    .orElseThrow(() -> new RuntimeException("Account not found."));


        if(type.equalsIgnoreCase("WITHDRAWAL")) {
            if (account.getBalance() .compareTo(amount) < 0.0) {
                throw new RuntimeException("Insufficient funds.");
            }
            account.setBalance(account.getBalance().subtract(amount));
            withdrawal.setType("WITHDRAWAL");
        }
        else{
            account.setBalance(account.getBalance().add(amount));
                withdrawal.setType("DEPOSIT");
        }
            accountRepository.save(account);


            withdrawal.setAccount(account);

            withdrawal.setAmount(amount);
            // withdrawal.setDescription(AESUtil.encrypt("Optional sensitive note"));
            transactionRepository.save(withdrawal);
        }

    @Override
    public String checkBalance(String AccountNum, Long userId) {
        Optional<Customer> byUserId = customerRepository.findByUserId(userId);
        if(byUserId.isEmpty()){
            return "Customer Details not found";
        }
        Optional<Account> byAccountNumber = accountRepository.findByAccountNumberAndUserId(AccountNum ,byUserId.get().getId());
        if(byAccountNumber.isPresent()){
            Account account = byAccountNumber.get();
            return "Your "+" "+ account.getAccountType()+" "+"Account Balance is "+account.getBalance();
        }
        return "Your Account is not present";
    }

    @Override
    public List<TransactionWithCustomerDTO> getAllTransactionHistory() {
        List<Map<String, Object>> rows = transactionRepository.fetchAllTransactionHistory();
        List<TransactionWithCustomerDTO> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (Map<String, Object> row : rows) {
            OffsetDateTime transactionTime;

            Object rawTimeObject = row.get("transactionTime");

            if (rawTimeObject == null) {
                transactionTime = null;
            } else if (rawTimeObject instanceof TIMESTAMPTZ) {
                try {
                    transactionTime = ((TIMESTAMPTZ) rawTimeObject).toOffsetDateTime();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to convert TIMESTAMPTZ to OffsetDateTime.", e);
                }
            } else if (rawTimeObject instanceof OffsetDateTime) {
                transactionTime = (OffsetDateTime) rawTimeObject;
            } else {
                throw new RuntimeException("Unexpected transactionTime type: " + rawTimeObject.getClass().getName());
            }

            String addressesJson = (String) row.get("customerAddressesJson");
            List<CustomerAddressDTO> customerAddresses = new ArrayList<>();

            if (addressesJson != null) {
                try {
                    customerAddresses = objectMapper.readValue(
                            addressesJson,
                            new TypeReference<List<CustomerAddressDTO>>() {}
                    );
                } catch (Exception e) {
                    System.err.println("Error parsing JSON address for transaction " + row.get("transactionId"));
                    e.printStackTrace();
                }
            }

            result.add(new TransactionWithCustomerDTO(
                    ((Number) row.get("transactionId")).longValue(),
                    (String) row.get("type"),
                    (BigDecimal) row.get("amount"),
                    transactionTime,
                    (String) row.get("description"),
                    (String) row.get("accountNumber"),
                    (String) row.get("customerFullName"),
                    (String) row.get("customerUsername"),
                    (String) row.get("customerEmail"),
                    (String) row.get("customerPhoneNumber"),
                    customerAddresses
            ));
        }
        return result;
    }
    }
