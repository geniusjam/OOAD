package ecommerce.service;

import ecommerce.domain.Admin;
import ecommerce.domain.Customer;
import ecommerce.domain.User;
import ecommerce.domain.UserRole;
import ecommerce.infrastructure.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {
    private final UserRepository userRepository;
    private int nextId = 1;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String email, String password, UserRole role) {
        if (username == null || username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return null;
        }
        if (password == null || password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return null;
        }
        if (userRepository.findByUsername(username) != null) {
            System.out.println("Username already taken.");
            return null;
        }
        String userId = "U" + nextId++;
        String hashed = hashPassword(password);
        User user = role == UserRole.ADMIN
                ? new Admin(userId, username, email, hashed)
                : new Customer(userId, username, email, hashed);
        userRepository.save(user);
        return user;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(hashPassword(password))) {
            System.out.println("Invalid username or password.");
            return null;
        }
        return user;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
