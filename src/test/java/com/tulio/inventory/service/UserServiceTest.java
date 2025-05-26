package com.tulio.inventory.service;

import com.tulio.inventory.dto.UserDTO;
import com.tulio.inventory.entity.User;
import com.tulio.inventory.exception.BadRequestException;
import com.tulio.inventory.exception.ResourceNotFoundException;
import com.tulio.inventory.repository.UserRepository;
import com.tulio.inventory.util.ErrorConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private final Long userId = 1L;
    private final String userName = "Test User";

    @BeforeEach
    void setUp() {
        // Setup test user entity
        testUser = new User();
        testUser.setId(userId);
        testUser.setName(userName);
        testUser.setAge(30);
        testUser.setPosition("Developer");
        testUser.setHireDate(LocalDate.now().minusDays(30));

        // Setup test user DTO
        testUserDTO = new UserDTO();
        testUserDTO.setId(userId);
        testUserDTO.setName(userName);
        testUserDTO.setAge(30);
        testUserDTO.setPosition("Developer");
        testUserDTO.setHireDate(LocalDate.now().minusDays(30));
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() {
        // Arrange
        when(userRepository.findByName(testUserDTO.getName())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO createdUser = userService.createUser(testUserDTO);

        // Assert
        assertNotNull(createdUser);
        assertEquals(testUserDTO.getName(), createdUser.getName());
        assertEquals(testUserDTO.getAge(), createdUser.getAge());
        assertEquals(testUserDTO.getPosition(), createdUser.getPosition());
        assertEquals(testUserDTO.getHireDate(), createdUser.getHireDate());
        verify(userRepository).findByName(testUserDTO.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateName_ShouldThrowBadRequestException() {
        // Arrange
        when(userRepository.findByName(testUserDTO.getName())).thenReturn(Optional.of(testUser));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.createUser(testUserDTO);
        });
        assertEquals(ErrorConstants.USUARIO_EXISTE_NOMBRE + testUserDTO.getName(), exception.getMessage());
        verify(userRepository).findByName(testUserDTO.getName());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        // Arrange
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setAge(35);
        updateDTO.setPosition("Senior Developer");
        updateDTO.setHireDate(LocalDate.now().minusDays(15));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName(updateDTO.getName());
        updatedUser.setAge(updateDTO.getAge());
        updatedUser.setPosition(updateDTO.getPosition());
        updatedUser.setHireDate(updateDTO.getHireDate());

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByName(updateDTO.getName())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDTO result = userService.updateUser(userId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getAge(), result.getAge());
        assertEquals(updateDTO.getPosition(), result.getPosition());
        assertEquals(updateDTO.getHireDate(), result.getHireDate());
        verify(userRepository).findById(userId);
        verify(userRepository).findByName(updateDTO.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_WithDuplicateName_ShouldThrowBadRequestException() {
        // Arrange
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Another User");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setName("Another User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.findByName(updateDTO.getName())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.updateUser(userId, updateDTO);
        });
        assertEquals(ErrorConstants.USUARIO_EXISTE_NOMBRE + updateDTO.getName(), exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).findByName(updateDTO.getName());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getAge(), result.getAge());
        assertEquals(testUser.getPosition(), result.getPosition());
        assertEquals(testUser.getHireDate(), result.getHireDate());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistingId = 999L;
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(nonExistingId);
        });
        assertEquals(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + nonExistingId, exception.getMessage());
        verify(userRepository).findById(nonExistingId);
    }

    @Test
    void deleteUser_WithExistingId_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Arrange
        Long nonExistingId = 999L;
        when(userRepository.existsById(nonExistingId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(nonExistingId);
        });
        assertEquals(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + nonExistingId, exception.getMessage());
        verify(userRepository).existsById(nonExistingId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setName("Second User");
        secondUser.setAge(25);
        secondUser.setPosition("Tester");
        secondUser.setHireDate(LocalDate.now().minusDays(60));

        List<User> userList = Arrays.asList(testUser, secondUser);
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testUser.getName(), result.get(0).getName());
        assertEquals(secondUser.getId(), result.get(1).getId());
        assertEquals(secondUser.getName(), result.get(1).getName());
        verify(userRepository).findAll();
    }
}

