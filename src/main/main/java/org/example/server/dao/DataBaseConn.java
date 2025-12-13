package org.example.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//     pg_ctl -D "C:\Program Files\pgsql\data" start
//
//     pg_ctl -D "C:\Program Files\pgsql\data" stop
//         Проверка процессов
//     tasklist | findstr postgres
//         Проверка порта
//     netstat -an | findstr :5432
public class DataBaseConn {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Postgres JDBC driver not found", e);
        }
    }

    // Читаем параметры соединения из системных свойств(web.xml) или возвращает дефолты
    public static Connection getConnection() throws SQLException {
        String url = System.getProperty("jdbc.url", "jdbc:postgresql://localhost:5432/user_db");
        String user = System.getProperty("jdbc.user", "postgres");
        String pass = System.getProperty("jdbc.password", "1234");
        return DriverManager.getConnection(url, user, pass);
    }
}
