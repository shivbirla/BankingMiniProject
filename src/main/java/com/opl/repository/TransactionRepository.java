package com.opl.repository;

import com.opl.domain.Account;
import com.opl.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountInAndTransactionTimeBetween(
            List<Account> accounts,
            Instant startTime,
            Instant endTime
    );

    @Query(value = """
                SELECT
                                                                t.id AS transactionId,
                                                                t.type AS type,
                                                                t.amount AS amount,
                                                                t.transaction_time AS transactionTime,
                                                                t.description AS description,
                                                                a.account_number AS accountNumber,
                                                                c.full_name AS customerFullName,
                                                                c.user_name AS customerUsername,
                                                                c.email AS customerEmail,
                                                                c.phone_number AS customerPhoneNumber,
                                                                (
                                                                    SELECT JSON_ARRAYAGG(
                                                                                JSON_OBJECT(
                                                                                    'addressLine1' VALUE cd.address_line1,
                                                                                    'addressLine2' VALUE cd.address_line2,
                                                                                    'addressType' VALUE cd.address_type,
                                                                                    'city' VALUE cd.city,
                                                                                    'country' VALUE cd.country,
                                                                                    'postalCode' VALUE cd.postal_code,
                                                                                    'state' VALUE cd.state
                                                                                )
                                                                            )
                                                                    FROM banking_schema.customer_addresses cd
                                                                    WHERE cd.customer_id = c.id
                                                                ) AS customerAddressesJson
                                                            FROM banking_schema.transactions t
                                                            INNER JOIN banking_schema.accounts a ON t.account_id = a.id
                                                            INNER JOIN banking_schema.customer c ON a.user_id = c.id
                                                            ORDER BY t.transaction_time DESC
            """, nativeQuery = true)
    List<Map<String, Object>> fetchAllTransactionHistory();
}
