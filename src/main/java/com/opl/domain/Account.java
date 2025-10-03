package com.opl.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ACCOUNTS",schema = "BANKING_SCHEMA")
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "account_number", length = 20, unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_type", length = 20)
    private String accountType;

    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
