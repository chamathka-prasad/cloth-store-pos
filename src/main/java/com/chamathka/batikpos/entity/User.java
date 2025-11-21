package com.chamathka.batikpos.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * User Entity - System Users (Admin and Cashier)
 *
 * Stores login and role information for system users as per SRS Table 1.
 * Implements FR-AUTH (User Authentication) requirements.
 *
 * As per FR-AUTH-02: Passwords are stored using BCrypt hashing.
 * As per FR-AUTH-03: Role-based access control (RBAC) is enforced.
 * As per FR-AUTH-05: The Admin user cannot be deleted.
 *
 * Roles:
 * - ADMIN: Full system access (Manager/Owner)
 * - CASHIER: Limited access (Sales transactions only)
 *
 * @author Batik POS Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "User")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the user.
     * Auto-generated primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    /**
     * The user's login name.
     * Must be unique and case-sensitive.
     * Maximum 50 characters.
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Hashed password using BCrypt algorithm.
     * Never store plain-text passwords (FR-AUTH-02).
     * Maximum 255 characters to accommodate BCrypt hash.
     */
    @Column(name = "passwordHash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User role for RBAC (Role-Based Access Control).
     * Valid values: "ADMIN" or "CASHIER"
     * Maximum 20 characters.
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    // ==================== Constructors ====================

    /**
     * Default no-argument constructor (required by JPA).
     */
    public User() {
    }

    /**
     * Constructor with all required fields.
     *
     * @param username     The login username (unique, case-sensitive)
     * @param passwordHash The BCrypt hashed password
     * @param role         The user role ("ADMIN" or "CASHIER")
     */
    public User(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ==================== Getters and Setters ====================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ==================== Business Logic Methods ====================

    /**
     * Checks if this user has Admin privileges.
     *
     * @return true if role is "ADMIN", false otherwise
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    /**
     * Checks if this user has Cashier privileges.
     *
     * @return true if role is "CASHIER", false otherwise
     */
    public boolean isCashier() {
        return "CASHIER".equalsIgnoreCase(this.role);
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
               Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
