package com.tulio.inventory;

import com.tulio.inventory.repository.ProductRepository;
import com.tulio.inventory.repository.UserRepository;
import com.tulio.inventory.service.ProductService;
import com.tulio.inventory.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class InventoryApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void contextLoads() {
        // Esta prueba verifica que el contexto de Spring se carga sin errores
    }

    @Test
    void servicesAreInjectedProperly() {
        // Verifica que los servicios se inyectan correctamente
        assertNotNull(userService, "UserService no fue inyectado correctamente");
        assertNotNull(productService, "ProductService no fue inyectado correctamente");
    }

    @Test
    void repositoriesAreInjectedProperly() {
        // Verifica que los repositorios se inyectan correctamente
        assertNotNull(userRepository, "UserRepository no fue inyectado correctamente");
        assertNotNull(productRepository, "ProductRepository no fue inyectado correctamente");
    }
}
