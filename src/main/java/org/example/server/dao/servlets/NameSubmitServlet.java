package org.example.server.dao.servlets;

import org.example.server.dao.DataBaseConn;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NameSubmitServlet extends HttpServlet {

    // SQL: при конфликте по имени — обновляем voted
    private static final String UPSERT_SQL =
            "INSERT INTO names_votes(name, voted) VALUES (?, ?) " +
                    "ON CONFLICT (name) DO UPDATE SET voted = EXCLUDED.voted";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String voteParam = req.getParameter("vote");
        boolean voted = "true".equalsIgnoreCase(voteParam) || "1".equals(voteParam);

        if (name == null || name.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing 'name' parameter");
            return;
        }

        try (Connection conn = DataBaseConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPSERT_SQL)) {

            ps.setString(1, name);
            ps.setBoolean(2, voted);
            ps.executeUpdate();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("ok");
        } catch (SQLException e) {
            throw new ServletException("hello"+e);
        }
    }
}