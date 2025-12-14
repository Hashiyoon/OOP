package org.example.server.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.server.dao.DataBaseConn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RestorePassServlet extends HttpServlet{
    private static final String UPSERT_SQL =
            "INSERT INTO users(login, password_hash) VALUES (?, ?) " +
                    "ON CONFLICT (login) DO UPDATE SET password_hash = EXCLUDED.password_hash";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String login = req.getParameter("login");
        String password_hash = req.getParameter("password_hash");

        try (Connection conn = DataBaseConn.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(UPSERT_SQL);

            ps.setString(1, login);
            ps.setString(2, password_hash);
            ps.executeUpdate();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("ok");
        } catch (SQLException e) {
            throw new ServletException("hello" + e);
        }
    }
}
