package com.opl.serviceImpl;

import com.opl.domain.Account;
import com.opl.domain.Transaction;
import com.opl.repository.AccountRepository;
import com.opl.repository.TransactionRepository;
import com.opl.request.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BankTransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public synchronized void executeTransfer(String sourceAcc, String destAcc, BigDecimal amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthResponse authResponse) {
            userId = authResponse.getId();
        }
        Account source = accountRepository.findByAccountNumberAndUserId(sourceAcc,userId)
                .orElseThrow(() -> new RuntimeException("Source account not found."));
        Account destination = accountRepository.findByAccountNumberAndUserId(destAcc, userId)
                .orElseThrow(() -> new RuntimeException("Destination account not found."));

        if (source.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds.");
        }

        // 1. Debit Source
        source.setBalance(source.getBalance().subtract(amount));
        accountRepository.save(source);
        transactionRepository.save(createTransaction(source, amount, "WITHDRAWAL"));

        // 2. Credit Destination
        destination.setBalance(destination.getBalance().add(amount));
        accountRepository.save(destination);
        transactionRepository.save(createTransaction(destination, amount, "DEPOSIT"));
    }

    private Transaction createTransaction(Account account, BigDecimal amount, String type) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType(type);
        return tx;
    }
}
