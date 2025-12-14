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

}