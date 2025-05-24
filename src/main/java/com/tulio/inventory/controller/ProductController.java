package com.tulio.inventory.controller;

import com.tulio.inventory.dto.ProductCreationDTO;
import com.tulio.inventory.dto.ProductResponseDTO;
import com.tulio.inventory.dto.ProductUpdateDTO;
import com.tulio.inventory.service.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductCreationDTO productCreationDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productCreationDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateDTO productUpdateDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(productId, productUpdateDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId, @RequestParam Long requestingUserId) {
        productService.deleteProduct(productId, requestingUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entryDate,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String productName) {
        List<ProductResponseDTO> products = productService.searchProducts(entryDate, userId, productName);
        return ResponseEntity.ok(products);
    }
}
