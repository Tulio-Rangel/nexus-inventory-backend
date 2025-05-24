package com.tulio.inventory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductResponseDTO {
    private Long id;
    private String productName;
    private Integer quantity;
    private LocalDate entryDate;
    private String registeredByName; // Nombre del usuario que registró
    private String lastModifiedByName; // Nombre del usuario que modificó
    private LocalDateTime lastModificationDate;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Long id, String productName, Integer quantity, LocalDate entryDate, String registeredByName, String lastModifiedByName, LocalDateTime lastModificationDate) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.entryDate = entryDate;
        this.registeredByName = registeredByName;
        this.lastModifiedByName = lastModifiedByName;
        this.lastModificationDate = lastModificationDate;
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

    public String getRegisteredByName() {
        return registeredByName;
    }

    public void setRegisteredByName(String registeredByName) {
        this.registeredByName = registeredByName;
    }

    public String getLastModifiedByName() {
        return lastModifiedByName;
    }

    public void setLastModifiedByName(String lastModifiedByName) {
        this.lastModifiedByName = lastModifiedByName;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(LocalDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
