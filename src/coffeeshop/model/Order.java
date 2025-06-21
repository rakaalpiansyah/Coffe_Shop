package coffeeshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status; 
    private List<OrderItem> items; 

    public Order(int id, int userId, LocalDateTime orderDate, BigDecimal totalAmount, String status) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Order(int userId, LocalDateTime orderDate, BigDecimal totalAmount, String status) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public List<OrderItem> getItems() { return items; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", userId=" + userId +
               ", orderDate=" + orderDate +
               ", totalAmount=" + totalAmount +
               ", status='" + status + '\'' +
               '}';
    }
}
