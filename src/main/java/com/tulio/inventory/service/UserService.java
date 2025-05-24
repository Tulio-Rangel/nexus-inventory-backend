package com.tulio.inventory.service;

import com.tulio.inventory.dto.UserDTO;
import com.tulio.inventory.entity.User;
import com.tulio.inventory.exception.BadRequestException;
import com.tulio.inventory.exception.ResourceNotFoundException;
import com.tulio.inventory.repository.UserRepository;
import com.tulio.inventory.util.ErrorConstants;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + id));
        return convertToDto(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
            throw new BadRequestException(ErrorConstants.NOMBRE_USUARIO_NO_PUEDER_ESTAR_VACIO);
        }
        if (userDTO.getAge() == null || userDTO.getAge() <= 0) {
            throw new BadRequestException(ErrorConstants.EDAD_USUARIO_DEBE_SER_POSITIVA);
        }
        if (userDTO.getPosition() == null || userDTO.getPosition().trim().isEmpty()) {
            throw new BadRequestException(ErrorConstants.CARGO_USUARIO_NO_PUEDER_ESTAR_VACIO);
        }
        if (userDTO.getHireDate() == null || userDTO.getHireDate().isAfter(LocalDate.now())) {
            throw new BadRequestException(ErrorConstants.FECHA_INGRESO_NO_PUEDE_SER_FUTURA);
        }
        if (userRepository.findByName(userDTO.getName()).isPresent()) {
            throw new BadRequestException(ErrorConstants.USUARIO_EXISTE_NOMBRE + userDTO.getName());
        }

        User user = convertToEntity(userDTO);
        user.setId(null);
        return convertToDto(userRepository.save(user));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + id));

        if (userDTO.getName() != null && !userDTO.getName().trim().isEmpty()) {
            if (!existingUser.getName().equals(userDTO.getName()) && userRepository.findByName(userDTO.getName()).isPresent()) {
                throw new BadRequestException(ErrorConstants.USUARIO_EXISTE_NOMBRE + userDTO.getName());
            }
            existingUser.setName(userDTO.getName());
        }
        if (userDTO.getAge() != null && userDTO.getAge() > 0) {
            existingUser.setAge(userDTO.getAge());
        }
        if (userDTO.getPosition() != null && !userDTO.getPosition().trim().isEmpty()) {
            existingUser.setPosition(userDTO.getPosition());
        }
        if (userDTO.getHireDate() != null && !userDTO.getHireDate().isAfter(LocalDate.now())) {
            existingUser.setHireDate(userDTO.getHireDate());
        }

        return convertToDto(userRepository.save(existingUser));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorConstants.USUARIO_NO_ENCONTRADO_ID + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getAge(), user.getPosition(), user.getHireDate());
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setAge(userDTO.getAge());
        user.setPosition(userDTO.getPosition());
        user.setHireDate(userDTO.getHireDate());
        return user;
    }
}
