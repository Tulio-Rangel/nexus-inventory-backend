package com.tulio.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tulio.inventory.dto.ProductCreationDTO;
import com.tulio.inventory.dto.ProductResponseDTO;
import com.tulio.inventory.dto.ProductUpdateDTO;
import com.tulio.inventory.exception.ResourceNotFoundException;
import com.tulio.inventory.exception.UnauthorizedActionException;
import com.tulio.inventory.service.ProductService;
import com.tulio.inventory.util.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductResponseDTO testProductDTO;
    private ProductCreationDTO testCreationDTO;
    private ProductUpdateDTO testUpdateDTO;
    private final Long productId = 1L;
    private final Long userId = 1L;
    private final Long differentUserId = 2L;

    @BeforeEach
    void setUp() {
        // Create test product response DTO
        testProductDTO = new ProductResponseDTO();
        testProductDTO.setId(productId);
        testProductDTO.setProductName("Test Product");
        testProductDTO.setQuantity(10);
        testProductDTO.setEntryDate(LocalDate.now().minusDays(5));
        testProductDTO.setRegisteredByName("Test User");
        testProductDTO.setLastModifiedByName(null);
        testProductDTO.setLastModificationDate(null);

        // Create test product creation DTO
        testCreationDTO = new ProductCreationDTO();
        testCreationDTO.setProductName("New Product");
        testCreationDTO.setQuantity(20);
        testCreationDTO.setEntryDate(LocalDate.now().minusDays(1));
        testCreationDTO.setRegisteredByUserId(userId);

        // Create test product update DTO
        testUpdateDTO = new ProductUpdateDTO();
        testUpdateDTO.setProductName("Updated Product");
        testUpdateDTO.setQuantity(15);
        testUpdateDTO.setEntryDate(LocalDate.now().minusDays(2));
        testUpdateDTO.setLastModifiedByUserId(userId);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        // Arrange
        ProductResponseDTO secondProductDTO = new ProductResponseDTO();
        secondProductDTO.setId(2L);
        secondProductDTO.setProductName("Second Product");
        secondProductDTO.setQuantity(20);
        secondProductDTO.setEntryDate(LocalDate.now().minusDays(10));
        secondProductDTO.setRegisteredByName("Another User");
        secondProductDTO.setLastModifiedByName(null);
        secondProductDTO.setLastModificationDate(null);

        List<ProductResponseDTO> productList = Arrays.asList(testProductDTO, secondProductDTO);
        when(productService.getAllProducts()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/products/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testProductDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].productName", is(testProductDTO.getProductName())))
                .andExpect(jsonPath("$[1].id", is(secondProductDTO.getId().intValue())))
                .andExpect(jsonPath("$[1].productName", is(secondProductDTO.getProductName())));

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_WithExistingId_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(productId)).thenReturn(testProductDTO);

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testProductDTO.getId().intValue())))
                .andExpect(jsonPath("$.productName", is(testProductDTO.getProductName())))
                .andExpect(jsonPath("$.quantity", is(testProductDTO.getQuantity())))
                .andExpect(jsonPath("$.registeredByName", is(testProductDTO.getRegisteredByName())));

        verify(productService).getProductById(productId);
    }

    @Test
    void getProductById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        Long nonExistingId = 999L;
        when(productService.getProductById(nonExistingId))
                .thenThrow(new ResourceNotFoundException(ErrorConstants.PRODUCTO_NO_ENCONTRADO_ID + nonExistingId));

        // Act & Assert
        mockMvc.perform(get("/api/products/{id}", nonExistingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService).getProductById(nonExistingId);
    }

    @Test
    void searchProducts_WithValidCriteria_ShouldReturnMatchingProducts() throws Exception {
        // Arrange
        LocalDate searchDate = LocalDate.now().minusDays(5);
        List<ProductResponseDTO> searchResults = Collections.singletonList(testProductDTO);
        
        when(productService.searchProducts(eq(searchDate), eq(userId), eq("Test")))
                .thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("entryDate", searchDate.toString())
                .param("userId", userId.toString())
                .param("productName", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testProductDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].productName", is(testProductDTO.getProductName())));

        verify(productService).searchProducts(eq(searchDate), eq(userId), eq("Test"));
    }

    @Test
    void createProduct_WithValidData_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        ProductResponseDTO createdProductDTO = new ProductResponseDTO();
        createdProductDTO.setId(3L);
        createdProductDTO.setProductName(testCreationDTO.getProductName());
        createdProductDTO.setQuantity(testCreationDTO.getQuantity());
        createdProductDTO.setEntryDate(testCreationDTO.getEntryDate());
        createdProductDTO.setRegisteredByName("Test User");
        createdProductDTO.setLastModifiedByName(null);
        createdProductDTO.setLastModificationDate(null);

        when(productService.createProduct(any(ProductCreationDTO.class))).thenReturn(createdProductDTO);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdProductDTO.getId().intValue())))
                .andExpect(jsonPath("$.productName", is(createdProductDTO.getProductName())))
                .andExpect(jsonPath("$.quantity", is(createdProductDTO.getQuantity())))
                .andExpect(jsonPath("$.registeredByName", is(createdProductDTO.getRegisteredByName())));

        verify(productService).createProduct(any(ProductCreationDTO.class));
    }

    @Test
    void updateProduct_WithValidData_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        ProductResponseDTO updatedProductDTO = new ProductResponseDTO();
        updatedProductDTO.setId(productId);
        updatedProductDTO.setProductName(testUpdateDTO.getProductName());
        updatedProductDTO.setQuantity(testUpdateDTO.getQuantity());
        updatedProductDTO.setEntryDate(testUpdateDTO.getEntryDate());
        updatedProductDTO.setRegisteredByName("Test User");
        updatedProductDTO.setLastModifiedByName("Test User");
        updatedProductDTO.setLastModificationDate(LocalDateTime.now());

        when(productService.updateProduct(eq(productId), any(ProductUpdateDTO.class))).thenReturn(updatedProductDTO);

        // Act & Assert
        mockMvc.perform(put("/api/products/{productId}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedProductDTO.getId().intValue())))
                .andExpect(jsonPath("$.productName", is(updatedProductDTO.getProductName())))
                .andExpect(jsonPath("$.quantity", is(updatedProductDTO.getQuantity())))
                .andExpect(jsonPath("$.lastModifiedByName", is(updatedProductDTO.getLastModifiedByName())));

        verify(productService).updateProduct(eq(productId), any(ProductUpdateDTO.class));
    }

    @Test
    void deleteProduct_ByCreator_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(productId, userId);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{productId}", productId)
                .param("requestingUserId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(productId, userId);
    }

    @Test
    void deleteProduct_ByNonCreator_ShouldReturnForbidden() throws Exception {
        // Arrange
        doThrow(new UnauthorizedActionException(ErrorConstants.USUARIO_CREADOR_DEBE_ELIMINAR))
                .when(productService).deleteProduct(productId, differentUserId);

        // Act & Assert
        mockMvc.perform(delete("/api/products/{productId}", productId)
                .param("requestingUserId", differentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(productService).deleteProduct(productId, differentUserId);
    }
}

