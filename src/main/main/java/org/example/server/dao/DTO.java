package org.example.server.dao;

import java.util.HashMap;
import java.util.Map;

//Стоит ли возвращать тип данных DTO? В api просто прописывать DTO имя_переменной = Класс_DTO
//Подумать: оставлять ли метод toMap() для каждой модели данных или унифицировать при помощи цикла
public class DTO {
    public interface MapConvetion{
        Map<String, String> toMap();
    }

    public static class RegisterRequest implements MapConvetion{
        final String login;
        final String password_hash;

        public RegisterRequest(String a, String b) {
            this.login = a;
            this.password_hash = b;
        }

        public String getLogin() {return login;}
        public String getPassword() {return password_hash;}
        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("login", getLogin());
            map.put("password_hash", getPassword());
            return map;
        }
    }

    public static class RestoreRequest implements MapConvetion {
        final String login;
        final String password;

        public RestoreRequest(String a, String b) {
            this.login = a;
            this.password = b;
        }

        public String getLogin() {return login;}
        public String getPassword() {return password;}
        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("login", getLogin());
            map.put("password_hash", getPassword());
            return map;
        }
    }

    public static class ProfileRequest implements MapConvetion {
        final String login;
        /*
        Все необходимые данные юзера с БД для окна профиля
         */
        public ProfileRequest(String a) {
            this.login = a;
            /*
            Конструктор для объекта
             */
        }

        public String getLogin() {return login; }
        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("login", getLogin());
            /*
            Все остальные данные для формирования выходного объекта
             */
            return map;
        }
    }
    public static class OrderRequest implements MapConvetion {
        final String login;
        final String orderId;
        final String productName;
        final int quantity;
        final double price;
        final String status; // "pending", "processing", "completed", "cancelled"
        final String createdAt;

        public OrderRequest(String login, String orderId, String productName, 
                           int quantity, double price, String status, String createdAt) {
            this.login = login;
            this.orderId = orderId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.status = status;
            this.createdAt = createdAt;
        }

        public String getLogin() { return login; }
        public String getOrderId() { return orderId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }

        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("login", getLogin());
            map.put("order_id", getOrderId());
            map.put("product_name", getProductName());
            map.put("quantity", String.valueOf(getQuantity()));
            map.put("price", String.valueOf(getPrice()));
            map.put("status", getStatus());
            map.put("created_at", getCreatedAt());
            return map;
        }
    }

    public static class CreateOrderRequest implements MapConvetion {
        final String login;
        final String productName;
        final int quantity;
        final double price;

        public CreateOrderRequest(String login, String productName, int quantity, double price) {
            this.login = login;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getLogin() { return login; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }

        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("login", getLogin());
            map.put("product_name", getProductName());
            map.put("quantity", String.valueOf(getQuantity()));
            map.put("price", String.valueOf(getPrice()));
            return map;
        }
    }

    public static class UpdateOrderStatusRequest implements MapConvetion {
        final String orderId;
        final String status;

        public UpdateOrderStatusRequest(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public String getOrderId() { return orderId; }
        public String getStatus() { return status; }

        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("order_id", getOrderId());
            map.put("status", getStatus());
            return map;
        }
    }

}
