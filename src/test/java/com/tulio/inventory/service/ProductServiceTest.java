package com.tulio.inventory.service;

import com.tulio.inventory.dto.ProductCreationDTO;
import com.tulio.inventory.dto.ProductResponseDTO;
import com.tulio.inventory.dto.ProductUpdateDTO;
import com.tulio.inventory.entity.Product;
import com.tulio.inventory.entity.User;
import com.tulio.inventory.exception.BadRequestException;
import com.tulio.inventory.exception.ResourceNotFoundException;
import com.tulio.inventory.exception.UnauthorizedActionException;
import com.tulio.inventory.repository.ProductRepository;
import com.tulio.inventory.repository.UserRepository;
import com.tulio.inventory.util.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private User testUser;
    private User anotherUser;
    private Product testProduct;
    private ProductCreationDTO testCreationDTO;
    private ProductUpdateDTO testUpdateDTO;
    private final Long productId = 1L;
    private final Long userId = 1L;
    private final Long anotherUserId = 2L;
    private final String productName = "Test Product";

    @BeforeEach
    void setUp() {
        // Setup test users
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test User");
        testUser.setAge(30);
        testUser.setPosition("Developer");
        testUser.setHireDate(LocalDate.now().minusDays(30));

        anotherUser = new User();
        anotherUser.setId(anotherUserId);
        anotherUser.setName("Another User");
        anotherUser.setAge(35);
        anotherUser.setPosition("Manager");
        anotherUser.setHireDate(LocalDate.now().minusDays(60));

        // Setup test product
        testProduct = new Product();
        testProduct.setId(productId);
        testProduct.setProductName(productName);
        testProduct.setQuantity(10);
        testProduct.setEntryDate(LocalDate.now().minusDays(5));
        testProduct.setRegisteredBy(testUser);

        // Setup test creation DTO
        testCreationDTO = new ProductCreationDTO();
        testCreationDTO.setProductName(productName);
        testCreationDTO.setQuantity(10);
        testCreationDTO.setEntryDate(LocalDate.now().minusDays(5));
        testCreationDTO.setRegisteredByUserId(userId);

        // Setup test update DTO
        testUpdateDTO = new ProductUpdateDTO();
        testUpdateDTO.setProductName("Updated Product");
        testUpdateDTO.setQuantity(20);
        testUpdateDTO.setEntryDate(LocalDate.now().minusDays(3));
        testUpdateDTO.setLastModifiedByUserId(userId);
    }

    @Test
    void createProduct_WithValidData_ShouldReturnCreatedProduct() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findByProductName(testCreationDTO.getProductName())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductResponseDTO result = productService.createProduct(testCreationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getProductName(), result.getProductName());
        assertEquals(testProduct.getQuantity(), result.getQuantity());
        assertEquals(testProduct.getEntryDate(), result.getEntryDate());
        assertEquals(testUser.getName(), result.getRegisteredByName());
        verify(productRepository).findByProductName(testCreationDTO.getProductName());
        verify(userRepository).findById(userId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WithDuplicateName_ShouldThrowBadRequestException() {
        // Arrange
        when(productRepository.findByProductName(testCreationDTO.getProductName())).thenReturn(Optional.of(testProduct));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productService.createProduct(testCreationDTO);
        });
        assertEquals(ErrorConstants.PRODUCTO_EXISTE_NOMBRE + testCreationDTO.getProductName(), exception.getMessage());
        verify(productRepository).findByProductName(testCreationDTO.getProductName());
        verify(userRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(productRepository.findByProductName(testCreationDTO.getProductName())).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(testCreationDTO);
        });
        assertEquals(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + userId, exception.getMessage());
        verify(productRepository).findByProductName(testCreationDTO.getProductName());
        verify(userRepository).findById(userId);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WithValidData_ShouldReturnUpdatedProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setProductName(testUpdateDTO.getProductName());
        updatedProduct.setQuantity(testUpdateDTO.getQuantity());
        updatedProduct.setEntryDate(testUpdateDTO.getEntryDate());
        updatedProduct.setRegisteredBy(testUser);
        updatedProduct.setLastModifiedBy(testUser);
        updatedProduct.setLastModificationDate(LocalDateTime.now());

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.findByProductName(testUpdateDTO.getProductName())).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ProductResponseDTO result = productService.updateProduct(productId, testUpdateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updatedProduct.getId(), result.getId());
        assertEquals(updatedProduct.getProductName(), result.getProductName());
        assertEquals(updatedProduct.getQuantity(), result.getQuantity());
        assertEquals(updatedProduct.getEntryDate(), result.getEntryDate());
        assertEquals(testUser.getName(), result.getLastModifiedByName());
        verify(productRepository).findById(productId);
        verify(userRepository).findById(userId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WithDuplicateName_ShouldThrowBadRequestException() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(2L);
        existingProduct.setProductName(testUpdateDTO.getProductName());

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.findByProductName(testUpdateDTO.getProductName())).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productService.updateProduct(productId, testUpdateDTO);
        });
        assertEquals(ErrorConstants.PRODUCTO_EXISTE_NOMBRE + testUpdateDTO.getProductName(), exception.getMessage());
        verify(productRepository).findById(productId);
        verify(productRepository).findByProductName(testUpdateDTO.getProductName());
        verify(userRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_ByCreator_ShouldDeleteProduct() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        // Act
        productService.deleteProduct(productId, userId);

        // Assert
        verify(productRepository).findById(productId);
        verify(productRepository).delete(testProduct);
    }

    @Test
    void deleteProduct_ByNonCreator_ShouldThrowUnauthorizedActionException() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        UnauthorizedActionException exception = assertThrows(UnauthorizedActionException.class, () -> {
            productService.deleteProduct(productId, anotherUserId);
        });
        assertEquals(ErrorConstants.USUARIO_CREADOR_DEBE_ELIMINAR, exception.getMessage());
        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void searchProducts_ByEntryDate_ShouldReturnMatchingProducts() {
        // Arrange
        LocalDate searchDate = LocalDate.now().minusDays(5);
        List<Product> productList = Collections.singletonList(testProduct);
        when(productRepository.findByEntryDate(searchDate)).thenReturn(productList);

        // Act
        List<ProductResponseDTO> results = productService.searchProducts(searchDate, null, null);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testProduct.getId(), results.get(0).getId());
        assertEquals(testProduct.getProductName(), results.get(0).getProductName());
        verify(productRepository).findByEntryDate(searchDate);
    }

    @Test
    void searchProducts_ByUser_ShouldReturnMatchingProducts() {
        // Arrange
        List<Product> productList = Collections.singletonList(testProduct);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findByRegisteredBy(testUser)).thenReturn(productList);

        // Act
        List<ProductResponseDTO> results = productService.searchProducts(null, userId, null);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testProduct.getId(), results.get(0).getId());
        assertEquals(testProduct.getProductName(), results.get(0).getProductName());
        verify(userRepository).findById(userId);
        verify(productRepository).findByRegisteredBy(testUser);
    }

    @Test
    void searchProducts_ByName_ShouldReturnMatchingProducts() {
        // Arrange
        String searchName = "Test";
        List<Product> productList = Collections.singletonList(testProduct);
        when(productRepository.findByProductNameContainingIgnoreCase(searchName)).thenReturn(productList);

        // Act
        List<ProductResponseDTO> results = productService.searchProducts(null, null, searchName);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testProduct.getId(), results.get(0).getId());
        assertEquals(testProduct.getProductName(), results.get(0).getProductName());
        verify(productRepository).findByProductNameContainingIgnoreCase(searchName);
    }

    @Test
    void searchProducts_ByCombinedCriteria_ShouldReturnMatchingProducts() {
        // Arrange
        LocalDate searchDate = LocalDate.now().minusDays(5);
        String searchName = "Test";
        List<Product> productList = Collections.singletonList(testProduct);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(productRepository.findByEntryDateAndRegisteredByAndProductNameContainingIgnoreCase(
                eq(searchDate), eq(testUser), eq(searchName))).thenReturn(productList);

        // Act
        List<ProductResponseDTO> results = productService.searchProducts(searchDate, userId, searchName);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testProduct.getId(), results.get(0).getId());
        assertEquals(testProduct.getProductName(), results.get(0).getProductName());
        verify(userRepository).findById(userId);
        verify(productRepository).findByEntryDateAndRegisteredByAndProductNameContainingIgnoreCase(
                eq(searchDate), eq(testUser), eq(searchName));
    }

    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // Act
        ProductResponseDTO result = productService.getProductById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getProductName(), result.getProductName());
        assertEquals(testProduct.getQuantity(), result.getQuantity());
        assertEquals(testProduct.getEntryDate(), result.getEntryDate());
        verify(productRepository).findById(productId);
    }

    @Test
    void getProductById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistingId = 999L;
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(nonExistingId);
        });
        assertEquals(ErrorConstants.PRODUCTO_NO_ENCONTRADO_ID + nonExistingId, exception.getMessage());
        verify(productRepository).findById(nonExistingId);
    }
}

