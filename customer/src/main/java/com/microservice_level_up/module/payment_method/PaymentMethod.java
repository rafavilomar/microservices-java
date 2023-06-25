package com.microservice_level_up.module.payment_method;

import com.microservice_level_up.module.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentMethod {
    @Id
    @SequenceGenerator(name = "payment_method_id_sequence", sequenceName = "payment_method_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_method_id_sequence")
    private Long id;

    @Column(nullable = false, length = 64)
    private String methodName;

    @Column(nullable = false, length = 16)
    private String cardNumber;

    @Column(nullable = false, length = 64)
    private String alias;

    @Column(nullable = false)
    private int expirationMonth;

    @Column(nullable = false)
    private int expirationYear;

    @Column(nullable = false)
    private int cvv;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @Column(nullable = false)
    private Customer customer;
}
