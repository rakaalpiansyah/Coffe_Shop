package coffeeshop.service;

import coffeeshop.dao.OrderDAO;
import coffeeshop.dao.OrderItemDAO;
import coffeeshop.model.MenuItem;
import coffeeshop.model.Order;
import coffeeshop.model.OrderItem;
import coffeeshop.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
    }

    public boolean createOrder(User user, Map<MenuItem, Integer> cartItems) {
        if (cartItems.isEmpty()) {
            System.out.println("Keranjang kosong, tidak dapat membuat pesanan.");
            return false;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map.Entry<MenuItem, Integer> entry : cartItems.entrySet()) {
            MenuItem menuItem = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal itemSubtotal = menuItem.getPrice().multiply(new BigDecimal(quantity));
            totalAmount = totalAmount.add(itemSubtotal);

            orderItems.add(new OrderItem(
                0, 
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getPrice(),
                quantity,
                itemSubtotal
            ));
        }

        Order newOrder = new Order(user.getId(), LocalDateTime.now(), totalAmount, "Completed"); 
        int orderId = orderDAO.addOrder(newOrder); 

        if (orderId != -1) {
            boolean allItemsAdded = true;
            for (OrderItem item : orderItems) {
                item.setOrderId(orderId); 
                if (!orderItemDAO.addOrderItem(item)) {
                    allItemsAdded = false;
                    System.err.println("Gagal menambahkan order item: " + item.getMenuItemName());
                }
            }
            return allItemsAdded;
        }
        return false;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = orderDAO.getOrdersByUserId(userId);
        for (Order order : orders) {
            List<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderDAO.getAllOrders();
        for (Order order : orders) {
            List<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    public BigDecimal getTotalSalesAmount() {
        List<Order> allOrders = orderDAO.getAllOrders();
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Order order : allOrders) {
            totalSales = totalSales.add(order.getTotalAmount());
        }
        return totalSales;
    }
}
