package com.clubsphere.dao;

import com.clubsphere.model.User;
import com.clubsphere.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    
    /**
     * Create necessary tables if they don't exist
     */
    public void setupDatabase() {
        String createUsersTable = 
                "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "enrollment_no VARCHAR(50), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            logger.info("Users table created or already exists");
        } catch (SQLException e) {
            logger.error("Error setting up database tables", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Create a new user
     * @param user The user to create
     * @return The created user with ID
     * @throws SQLException if database operation fails
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, enrollment_no) VALUES (?, ?, ?, ?)";
        
        // Hash the password before storing
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, user.getEnrollmentNo());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    // Don't return the hashed password to caller
                    user.setPassword(null);
                    return user;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Authenticate a user
     * @param email User's email
     * @param password User's password
     * @return User object if authenticated, null otherwise
     * @throws SQLException if database operation fails
     */
    public User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, password, enrollment_no FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Get the stored hashed password
                    String storedHash = rs.getString("password");
                    
                    // Check if the provided password matches the stored hash
                    if (BCrypt.checkpw(password, storedHash)) {
                        User user = new User();
                        user.setId(rs.getLong("id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setEnrollmentNo(rs.getString("enrollment_no"));
                        // Don't set password in returned object
                        return user;
                    }
                }
                // Authentication failed
                return null;
            }
        }
    }
    
    /**
     * Find a user by email
     * @param email User's email
     * @return User object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, enrollment_no FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setEnrollmentNo(rs.getString("enrollment_no"));
                    return user;
                } else {
                    return null;
                }
            }
        }
    }
    
    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     * @throws SQLException if database operation fails
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true if email exists, false otherwise
            }
        }
    }
}
public class UserDAO {
    
}
