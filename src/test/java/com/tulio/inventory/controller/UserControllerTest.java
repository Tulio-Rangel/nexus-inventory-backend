package com.tulio.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tulio.inventory.dto.UserDTO;
import com.tulio.inventory.exception.ResourceNotFoundException;
import com.tulio.inventory.service.UserService;
import com.tulio.inventory.util.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDTO testUserDTO;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        // Create test user DTO
        testUserDTO = new UserDTO();
        testUserDTO.setId(userId);
        testUserDTO.setName("Test User");
        testUserDTO.setAge(30);
        testUserDTO.setPosition("Developer");
        testUserDTO.setHireDate(LocalDate.now().minusDays(30));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Arrange
        UserDTO secondUserDTO = new UserDTO();
        secondUserDTO.setId(2L);
        secondUserDTO.setName("Second User");
        secondUserDTO.setAge(25);
        secondUserDTO.setPosition("Tester");
        secondUserDTO.setHireDate(LocalDate.now().minusDays(60));

        List<UserDTO> userList = Arrays.asList(testUserDTO, secondUserDTO);
        when(userService.getAllUsers()).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testUserDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(testUserDTO.getName())))
                .andExpect(jsonPath("$[1].id", is(secondUserDTO.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(secondUserDTO.getName())));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.getUserById(userId)).thenReturn(testUserDTO);

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUserDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testUserDTO.getName())))
                .andExpect(jsonPath("$.age", is(testUserDTO.getAge())))
                .andExpect(jsonPath("$.position", is(testUserDTO.getPosition())));

        verify(userService).getUserById(userId);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        Long nonExistingId = 999L;
        when(userService.getUserById(nonExistingId))
                .thenThrow(new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + nonExistingId));

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", nonExistingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(nonExistingId);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        UserDTO createUserDTO = new UserDTO();
        createUserDTO.setName("New User");
        createUserDTO.setAge(28);
        createUserDTO.setPosition("Designer");
        createUserDTO.setHireDate(LocalDate.now().minusDays(10));

        UserDTO createdUserDTO = new UserDTO();
        createdUserDTO.setId(3L);
        createdUserDTO.setName(createUserDTO.getName());
        createdUserDTO.setAge(createUserDTO.getAge());
        createdUserDTO.setPosition(createUserDTO.getPosition());
        createdUserDTO.setHireDate(createUserDTO.getHireDate());

        when(userService.createUser(any(UserDTO.class))).thenReturn(createdUserDTO);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdUserDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(createdUserDTO.getName())))
                .andExpect(jsonPath("$.age", is(createdUserDTO.getAge())))
                .andExpect(jsonPath("$.position", is(createdUserDTO.getPosition())));

        verify(userService).createUser(any(UserDTO.class));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        UserDTO updateUserDTO = new UserDTO();
        updateUserDTO.setName("Updated Name");
        updateUserDTO.setAge(35);
        updateUserDTO.setPosition("Senior Developer");
        updateUserDTO.setHireDate(LocalDate.now().minusDays(15));

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(userId);
        updatedUserDTO.setName(updateUserDTO.getName());
        updatedUserDTO.setAge(updateUserDTO.getAge());
        updatedUserDTO.setPosition(updateUserDTO.getPosition());
        updatedUserDTO.setHireDate(updateUserDTO.getHireDate());

        when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(updatedUserDTO);

        // Act & Assert
        mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedUserDTO.getName())))
                .andExpect(jsonPath("$.age", is(updatedUserDTO.getAge())))
                .andExpect(jsonPath("$.position", is(updatedUserDTO.getPosition())));

        verify(userService).updateUser(eq(userId), any(UserDTO.class));
    }

    @Test
    void deleteUser_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }
}

