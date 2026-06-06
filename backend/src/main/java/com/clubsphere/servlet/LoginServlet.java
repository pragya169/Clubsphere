package com.clubsphere.servlet;

import com.clubsphere.dao.UserDAO;
import com.clubsphere.model.User;
import com.clubsphere.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for handling user login requests
 */
@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final UserDAO userDAO = new UserDAO();
    
    @Override
    public void init() throws ServletException {
        // Make sure the database table is set up
        userDAO.setupDatabase();
    }
    
    /**
     * Handle POST request for login
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        
        try {
            // Parse login credentials from request
            LoginRequest loginRequest = JsonUtil.parseRequestBody(request, LoginRequest.class);
            
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Email and password are required");
                return;
            }
            
            // Authenticate user
            User authenticatedUser = userDAO.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            
            if (authenticatedUser != null) {
                // Create session
                HttpSession session = request.getSession(true);
                session.setAttribute("user", authenticatedUser);
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                // Create success response
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("success", true);
                successResponse.put("message", "Login successful");
                successResponse.put("user", authenticatedUser);
                
                response.getWriter().write(JsonUtil.toJson(successResponse));
                logger.info("User logged in: {}", authenticatedUser.getEmail());
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
            }
        } catch (SQLException e) {
            logger.error("Database error during login", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        } catch (Exception e) {
            logger.error("Error during login", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        }
    }
    
    /**
     * Send error response to client
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        response.getWriter().write(JsonUtil.toJson(errorResponse));
    }
    
    /**
     * Inner class to represent login request
     */
    static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
public class LoginServlet {
    
}
