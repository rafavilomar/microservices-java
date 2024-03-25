package com.microservice_level_up.module.invoice.entity;

import com.microservice_level_up.module.invoice.enums.InvoiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Invoice {
    @Id
    @SequenceGenerator(name = "invoice_id_sequence", sequenceName = "invoice_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_id_sequence")
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType type;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double tax;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Builder.Default
    @OneToMany(mappedBy = "invoice")
    private List<Product> products = new ArrayList<>();
}
