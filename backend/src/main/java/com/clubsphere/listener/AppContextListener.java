package com.clubsphere.listener;

import com.clubsphere.dao.UserDAO;
import com.clubsphere.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application context listener for initialization and cleanup
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("ClubSphere application starting up...");
        
        try {
            // Initialize database tables
            new UserDAO().setupDatabase();
            logger.info("Database initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing database", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("ClubSphere application shutting down...");
        
        // Close database connection pool
        try {
            DatabaseUtil.closePool();
        } catch (Exception e) {
            logger.error("Error closing database connection pool", e);
        }
    }
}
public class AppContextListener {
    
}
