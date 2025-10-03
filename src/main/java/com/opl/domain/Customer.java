package com.opl.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMER" , schema = "BANKING_SCHEMA")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "user_name", length = 100)
    private String username;

    @Column(name = "full_name", length = 100)
    private String fullName;

    // email (VARCHAR(100))
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<CustomerAddress> addresses = new ArrayList<>();
}