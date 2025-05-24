package com.tulio.inventory.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate; // Fecha de ingreso

    @ManyToOne
    @JoinColumn(name = "registered_by_user_id", nullable = false)
    private User registeredBy; // Usuario que realiza el registro

    @ManyToOne
    @JoinColumn(name = "last_modified_by_user_id")
    private User lastModifiedBy; // Usuario que realiza la última modificación

    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate; // Fecha y hora de la última modificación

    public Product() {
    }

    public Product(String productName, Integer quantity, LocalDate entryDate, User registeredBy) {
        this.productName = productName;
        this.quantity = quantity;
        this.entryDate = entryDate;
        this.registeredBy = registeredBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public User getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(User registeredBy) {
        this.registeredBy = registeredBy;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(LocalDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
