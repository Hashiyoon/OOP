package org.example.server.servlets;

import org.example.server.dao.DataBaseConn;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogInServlet extends HttpServlet {

    // поиск по логину в таблице
    private static final String SELECT_SQL =
            "SELECT password_hash FROM users WHERE login = ?";
    // новая строка: пользователь - пароль
    private static final String INSERT_SQL =
            "INSERT INTO users(login, password_hash) VALUES (?, ?)";
    private static final String INSERT_OR_SQL =
            "INSERT INTO orders(login) VALUES (?)";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String login = req.getParameter("login");
        String password_hash = req.getParameter("password_hash");


        try (Connection conn = DataBaseConn.getConnection()) {

            try (PreparedStatement selectPs = conn.prepareStatement(SELECT_SQL)) {
                selectPs.setString(1, login);
                try (ResultSet rs = selectPs.executeQuery()) {

                    if (rs.next()) {
                        String password = rs.getString("password_hash");

                        if (password.equals(password_hash)) {
                            resp.setStatus(HttpServletResponse.SC_OK);
                            resp.getWriter().println("login=" + login);
                        } else {
                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            resp.getWriter().println("неподходящее имя или пароль :(");
                        }

                    } else {
                        try (PreparedStatement insertPs = conn.prepareStatement(INSERT_SQL)) {
                            conn.prepareStatement(INSERT_OR_SQL).setString(1, login);
                            insertPs.setString(1, login);
                            insertPs.setString(2, password_hash);
                            insertPs.executeUpdate();

                            resp.setStatus(HttpServletResponse.SC_OK);
                            resp.getWriter().println("login=" + login);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("hello" + e);
        }
    }
}