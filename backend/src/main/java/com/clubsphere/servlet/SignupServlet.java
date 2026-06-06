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
 * Servlet for handling user signup requests
 */
@WebServlet("/api/auth/signup")
public class SignupServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SignupServlet.class);
    private final UserDAO userDAO = new UserDAO();
    
    @Override
    public void init() throws ServletException {
        // Make sure the database table is set up
        userDAO.setupDatabase();
    }
    
    /**
     * Handle POST request for user registration
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        
        try {
            // Parse user data from request
            SignupRequest signupRequest = JsonUtil.parseRequestBody(request, SignupRequest.class);
            
            // Validate input
            if (signupRequest.getName() == null || signupRequest.getEmail() == null || 
                signupRequest.getPassword() == null) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Name, email, and password are required");
                return;
            }
            
            // Check if password meets minimum criteria
            if (signupRequest.getPassword().length() < 8) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Password must be at least 8 characters long");
                return;
            }
            
            // Check if email already exists
            if (userDAO.emailExists(signupRequest.getEmail())) {
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Email already in use");
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setName(signupRequest.getName());
            newUser.setEmail(signupRequest.getEmail());
            newUser.setPassword(signupRequest.getPassword());
            newUser.setEnrollmentNo(signupRequest.getEnrollmentNo());
            
            User createdUser = userDAO.createUser(newUser);
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", createdUser);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            // Create success response
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Registration successful");
            successResponse.put("user", createdUser);
            
            response.getWriter().write(JsonUtil.toJson(successResponse));
            logger.info("New user registered: {}", createdUser.getEmail());
            
        } catch (SQLException e) {
            logger.error("Database error during registration", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred");
        } catch (Exception e) {
            logger.error("Error during registration", e);
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
     * Inner class to represent signup request
     */
    static class SignupRequest {
        private String name;
        private String email;
        private String password;
        private String enrollmentNo;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
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
        
        public String getEnrollmentNo() {
            return enrollmentNo;
        }
        
        public void setEnrollmentNo(String enrollmentNo) {
            this.enrollmentNo = enrollmentNo;
        }
    }
}
public class SignupServlet {
    
}
