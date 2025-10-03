package com.opl.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "TRANSACTIONS",schema = "BANKING_SCHEMA")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @ToString.Exclude
    private Account account;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_time", columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false)
    private Instant transactionTime = Instant.now();

    @Lob
    @Column(name = "description")
    private String description;
}
