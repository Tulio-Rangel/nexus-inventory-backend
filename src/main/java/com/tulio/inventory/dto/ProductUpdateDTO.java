package com.tulio.inventory.dto;

import java.time.LocalDate;

public class ProductUpdateDTO {
    private String productName;
    private Integer quantity;
    private LocalDate entryDate;
    private Long lastModifiedByUserId; // ID del usuario que modifica

    public ProductUpdateDTO() {
    }

    public ProductUpdateDTO(String productName, Integer quantity, LocalDate entryDate, Long lastModifiedByUserId) {
        this.productName = productName;
        this.quantity = quantity;
        this.entryDate = entryDate;
        this.lastModifiedByUserId = lastModifiedByUserId;
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

    public Long getLastModifiedByUserId() {
        return lastModifiedByUserId;
    }

    public void setLastModifiedByUserId(Long lastModifiedByUserId) {
        this.lastModifiedByUserId = lastModifiedByUserId;
    }
}
