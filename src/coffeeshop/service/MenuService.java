package coffeeshop.service;

import coffeeshop.dao.MenuItemDAO;
import coffeeshop.model.MenuItem; 

import java.util.List;

public class MenuService {
    private MenuItemDAO menuItemDAO;

    public MenuService() {
        this.menuItemDAO = new MenuItemDAO();
    }

    public boolean addMenuItem(MenuItem item) {
        return menuItemDAO.addMenuItem(item);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemDAO.getAllMenuItems();
    }

    public MenuItem getMenuItemById(int id) {
        return menuItemDAO.getMenuItemById(id);
    }

    public boolean updateMenuItem(MenuItem item) {
        return menuItemDAO.updateMenuItem(item);
    }

    public boolean deleteMenuItem(int id) {
        return menuItemDAO.deleteMenuItem(id);
    }
}
