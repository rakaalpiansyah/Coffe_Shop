package coffeeshop.model;

import java.math.BigDecimal;

public class OrderItem {
    private int id;
    private int orderId;
    private int menuItemId;
    private String menuItemName; 
    private BigDecimal menuItemPrice;
    private int quantity;
    private BigDecimal subtotal;

    public OrderItem(int id, int orderId, int menuItemId, String menuItemName, BigDecimal menuItemPrice, int quantity, BigDecimal subtotal) {
        this.id = id;
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.menuItemPrice = menuItemPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public OrderItem(int orderId, int menuItemId, String menuItemName, BigDecimal menuItemPrice, int quantity, BigDecimal subtotal) {
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.menuItemPrice = menuItemPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Getters
    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getMenuItemId() { return menuItemId; }
    public String getMenuItemName() { return menuItemName; }
    public BigDecimal getMenuItemPrice() { return menuItemPrice; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }
    public void setMenuItemPrice(BigDecimal menuItemPrice) { this.menuItemPrice = menuItemPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "OrderItem{" +
               "id=" + id +
               ", orderId=" + orderId +
               ", menuItemId=" + menuItemId +
               ", menuItemName='" + menuItemName + '\'' +
               ", quantity=" + quantity +
               ", subtotal=" + subtotal +
               '}';
    }
}
