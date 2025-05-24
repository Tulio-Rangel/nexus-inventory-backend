package com.tulio.inventory.dto;

import java.time.LocalDate;

public class ProductCreationDTO {
    private String productName;
    private Integer quantity;
    private LocalDate entryDate;
    private Long registeredByUserId; // ID del usuario que registra

    public ProductCreationDTO() {
    }

    public ProductCreationDTO(String productName, Integer quantity, LocalDate entryDate, Long registeredByUserId) {
        this.productName = productName;
        this.quantity = quantity;
        this.entryDate = entryDate;
        this.registeredByUserId = registeredByUserId;
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

    public Long getRegisteredByUserId() {
        return registeredByUserId;
    }

    public void setRegisteredByUserId(Long registeredByUserId) {
        this.registeredByUserId = registeredByUserId;
    }
}
