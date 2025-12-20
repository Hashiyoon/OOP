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

    public static class CreateUserOrdersRequest implements MapConvetion {
        final String action;
        final String login;
        final String product_name;
        final String quantity;
        final String price;

        public CreateUserOrdersRequest(
                String action, String login, String product_name, String quantity, String price
        ) {
            this.action = action;
            this.login = login;
            this.product_name = product_name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getAction() {return action; }
        public String getLogin() {return login; }
        public String getProduct_name() {return product_name; }
        public String getQuantity() {return quantity; }
        public String getPrice() {return price; }

        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("action", getAction());
            map.put("login", getLogin());
            map.put("product_name", getProduct_name());
            map.put("quantity", getQuantity());
            map.put("price", getPrice());
            return map;
        }
    }

    public static class GetUserOrdersRequest implements MapConvetion {
        final String login;

        public GetUserOrdersRequest(String login) {
            this.login = login;
        }

        public String getLogin() {return login; }

        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("login", getLogin());
            return map;
        }
    }

    public static class UpdateUserOrdersRequest implements MapConvetion {
        final String action;
        final String order_id;
        final String status;

        public UpdateUserOrdersRequest(
                String action, String order_id, String status
        ) {
            this.action = action;
            this.order_id = order_id;
            this.status = status;
        }

        public String getAction() {return action; }
        public String getOrder_id() {return order_id; }
        public String getStatus() {return status; }


        @Override
        public Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            map.put("action", getAction());
            map.put("order_id", getOrder_id());
            map.put("status", getStatus());
            return map;
        }
    }


}