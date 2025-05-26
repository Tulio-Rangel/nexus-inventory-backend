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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ProductResponseDTO createProduct(ProductCreationDTO productCreationDTO) {
        // Validaciones de datos
        productValidator(productCreationDTO.getProductName(), productCreationDTO.getQuantity(), productCreationDTO.getEntryDate());
        if (productCreationDTO.getRegisteredByUserId() == null) {
            throw new BadRequestException(ErrorConstants.ESPECIFICAR_USUARIO);
        }

        // Verificar si ya existe un producto con el mismo nombre
        if (productRepository.findByProductName(productCreationDTO.getProductName()).isPresent()) {
            throw new BadRequestException(ErrorConstants.PRODUCTO_EXISTE_NOMBRE + productCreationDTO.getProductName());
        }

        User registeredByUser = userRepository.findById(productCreationDTO.getRegisteredByUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + productCreationDTO.getRegisteredByUserId()));

        Product product = new Product();
        product.setProductName(productCreationDTO.getProductName());
        product.setQuantity(productCreationDTO.getQuantity());
        product.setEntryDate(productCreationDTO.getEntryDate());
        product.setRegisteredBy(registeredByUser);

        return convertToResponseDto(productRepository.save(product));
    }

    public ProductResponseDTO updateProduct(Long productId, ProductUpdateDTO productUpdateDTO) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.PRODUCTO_NO_ENCONTRADO_ID + productId));

        // Validaciones de datos
        productValidator(productUpdateDTO.getProductName(), productUpdateDTO.getQuantity(), productUpdateDTO.getEntryDate());
        if (productUpdateDTO.getLastModifiedByUserId() == null) {
            throw new BadRequestException(ErrorConstants.ESPECIFICAR_USUARIO_MODIFICACION);
        }

        // Verificar si el nombre del producto está siendo cambiado a un nombre existente
        if (!existingProduct.getProductName().equals(productUpdateDTO.getProductName()) && productRepository.findByProductName(productUpdateDTO.getProductName()).isPresent()) {
                throw new BadRequestException(ErrorConstants.PRODUCTO_EXISTE_NOMBRE + productUpdateDTO.getProductName());
            }


        User lastModifiedByUser = userRepository.findById(productUpdateDTO.getLastModifiedByUserId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + productUpdateDTO.getLastModifiedByUserId()));

        existingProduct.setProductName(productUpdateDTO.getProductName());
        existingProduct.setQuantity(productUpdateDTO.getQuantity());
        existingProduct.setEntryDate(productUpdateDTO.getEntryDate());
        existingProduct.setLastModifiedBy(lastModifiedByUser);
        existingProduct.setLastModificationDate(LocalDateTime.now());

        return convertToResponseDto(productRepository.save(existingProduct));
    }

    public void deleteProduct(Long productId, Long requestingUserId) {
        Product productToDelete = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.PRODUCTO_NO_ENCONTRADO_ID + productId));

        if (!productToDelete.getRegisteredBy().getId().equals(requestingUserId)) {
            throw new UnauthorizedActionException(ErrorConstants.USUARIO_CREADOR_DEBE_ELIMINAR);
        }

        productRepository.delete(productToDelete);
    }

    public List<ProductResponseDTO> searchProducts(LocalDate entryDate, Long userId, String productName) {
        if (entryDate == null && userId == null && (productName == null || productName.trim().isEmpty())) {
            throw new BadRequestException(ErrorConstants.FILTRO_BUSQUEDA_VACIO);
        }

        List<Product> products;
        if (entryDate != null && userId != null && productName != null && !productName.trim().isEmpty()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + userId));
            products = productRepository.findByEntryDateAndRegisteredByAndProductNameContainingIgnoreCase(entryDate, user, productName);
        } else if (entryDate != null && userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + userId));
            products = productRepository.findByEntryDateAndRegisteredBy(entryDate, user);
        } else if (entryDate != null && productName != null && !productName.trim().isEmpty()) {
            products = productRepository.findByEntryDateAndProductNameContainingIgnoreCase(entryDate, productName);
        } else if (userId != null && productName != null && !productName.trim().isEmpty()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + userId));
            products = productRepository.findByRegisteredByAndProductNameContainingIgnoreCase(user, productName);
        } else if (entryDate != null) {
            products = productRepository.findByEntryDate(entryDate);
        } else if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID+ userId));
            products = productRepository.findByRegisteredBy(user);
        } else {
            products = productRepository.findByProductNameContainingIgnoreCase(productName);
        }

        return products.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.PRODUCTO_NO_ENCONTRADO_ID + id));
        return convertToResponseDto(product);
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // Validaciones comunes para creación y actualización de productos
    private void productValidator(String productName, Integer quantity, LocalDate entryDate) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new BadRequestException(ErrorConstants.NOMBRE_PRODUCTO_NO_PUEDE_SER_VACIO);
        }
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException(ErrorConstants.CANTIDAD_PRODUCTO_DEBE_SER_POSITIVA);
        }
        if (entryDate == null || entryDate.isAfter(LocalDate.now())) {
            throw new BadRequestException(ErrorConstants.FECHA_INGRESO_NO_PUEDE_SER_FUTURA);
        }
    }

    private ProductResponseDTO convertToResponseDto(Product product) {
        String registeredByName = product.getRegisteredBy() != null ? product.getRegisteredBy().getName() : null;
        String lastModifiedByName = product.getLastModifiedBy() != null ? product.getLastModifiedBy().getName() : null;

        return new ProductResponseDTO(
                product.getId(),
                product.getProductName(),
                product.getQuantity(),
                product.getEntryDate(),
                registeredByName,
                lastModifiedByName,
                product.getLastModificationDate()
        );
    }
}
