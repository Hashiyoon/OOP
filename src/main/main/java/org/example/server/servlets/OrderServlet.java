package org.example.server.servlets;

import org.example.server.dao.DataBaseConn;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderServlet extends HttpServlet {
    
    private static final String CREATE_ORDER_SQL = 
        "INSERT INTO orders (order_id, login, product_name, quantity, price, status) VALUES (?, ?, ?, ?, ?, 'pending')";
    
    private static final String GET_ORDERS_BY_USER_SQL = 
        "SELECT order_id, product_name, quantity, price, status, created_at FROM orders WHERE login = ? ORDER BY created_at DESC";
    
    private static final String GET_ORDER_BY_ID_SQL = 
        "SELECT order_id, login, product_name, quantity, price, status, created_at FROM orders WHERE order_id = ?";
    
    private static final String UPDATE_ORDER_STATUS_SQL = 
        "UPDATE orders SET status = ? WHERE order_id = ?";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        
        if ("create".equals(action)) {
            createOrder(req, resp);
        } else if ("update_status".equals(action)) {
            updateOrderStatus(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Неизвестное действие: " + action);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");
        String login = req.getParameter("login");
        String orderId = req.getParameter("order_id");
        
        if (login != null && !login.trim().isEmpty()) {
            getOrdersByUser(login, resp);
        } else if (orderId != null && !orderId.trim().isEmpty()) {
            getOrderById(orderId, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Необходим параметр login или order_id");
        }
    }

    private void createOrder(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String login = req.getParameter("login");
        String productName = req.getParameter("product_name");
        String quantityStr = req.getParameter("quantity");
        String priceStr = req.getParameter("price");
        
        if (login == null || productName == null || quantityStr == null || priceStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Отсутствуют обязательные параметры");
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            String orderId = UUID.randomUUID().toString();
            
            try (Connection conn = DataBaseConn.getConnection();
                 PreparedStatement ps = conn.prepareStatement(CREATE_ORDER_SQL)) {
                
                ps.setString(1, orderId);
                ps.setString(2, login);
                ps.setString(3, productName);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);
                
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("order_id=" + orderId);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().println("Не удалось создать заказ");
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Неверный формат числа: " + e.getMessage());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Ошибка базы данных: " + e.getMessage());
        }
    }

    private void getOrdersByUser(String login, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try (Connection conn = DataBaseConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ORDERS_BY_USER_SQL)) {
            
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                
                List<String> orders = new ArrayList<>();
                while (rs.next()) {
                    String order = String.format(
                        "order_id=%s&product_name=%s&quantity=%d&price=%.2f&status=%s&created_at=%s",
                        rs.getString("order_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                    );
                    orders.add(order);
                }
                
                resp.setStatus(HttpServletResponse.SC_OK);
                if (!orders.isEmpty()) {
                    resp.getWriter().println(String.join("\n", orders));
                } else {
                    resp.getWriter().println("Заказов не найдено");
                }
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Ошибка базы данных: " + e.getMessage());
        }
    }

    private void getOrderById(String orderId, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        try (Connection conn = DataBaseConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ORDER_BY_ID_SQL)) {
            
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                
                if (rs.next()) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(
                        String.format(
                            "order_id=%s&login=%s&product_name=%s&quantity=%d&price=%.2f&status=%s&created_at=%s",
                            rs.getString("order_id"),
                            rs.getString("login"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                        )
                    );
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().println("Заказ не найден");
                }
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Ошибка базы данных: " + e.getMessage());
        }
    }

    private void updateOrderStatus(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String orderId = req.getParameter("order_id");
        String status = req.getParameter("status");
        
        if (orderId == null || status == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Отсутствуют обязательные параметры");
            return;
        }
        
        try (Connection conn = DataBaseConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_ORDER_STATUS_SQL)) {
            
            ps.setString(1, status);
            ps.setString(2, orderId);
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("Статус обновлен");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("Заказ не найден");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Ошибка базы данных: " + e.getMessage());
        }
    }
}
