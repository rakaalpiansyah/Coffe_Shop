package coffeeshop.service;

import coffeeshop.dao.UserDAO;
import coffeeshop.model.User; 

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User login(String email, String password) {
        User user = userDAO.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean register(String name, String email, String password) {
        if (userDAO.getUserByEmail(email) != null) {
            return false;
        }
        User newUser = new User(name, email, password, false);
        return userDAO.registerUser(newUser);
    }
}
