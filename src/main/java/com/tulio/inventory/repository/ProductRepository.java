package com.tulio.inventory.repository;

import com.tulio.inventory.entity.Product;
import com.tulio.inventory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductName(String productName);
    List<Product> findByEntryDate(LocalDate entryDate);
    List<Product> findByRegisteredBy(User registeredBy);
    List<Product> findByProductNameContainingIgnoreCase(String productName); // Búsqueda de nombre de producto sin distinción de mayúsculas y minúsculas

    List<Product> findByEntryDateAndRegisteredByAndProductNameContainingIgnoreCase(LocalDate entryDate, User registeredBy, String productName);
    List<Product> findByEntryDateAndRegisteredBy(LocalDate entryDate, User registeredBy);
    List<Product> findByEntryDateAndProductNameContainingIgnoreCase(LocalDate entryDate, String productName);
    List<Product> findByRegisteredByAndProductNameContainingIgnoreCase(User registeredBy, String productName);
}
