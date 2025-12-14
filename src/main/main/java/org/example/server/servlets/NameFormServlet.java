//Староооеееее для веба
//Староооеееее для веба
//Староооеееее для веба
//Староооеееее для веба
//Староооеееее для веба
package org.example.server.servlets;


import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class NameFormServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!doctype html>");
        out.println("<html><head><meta charset='utf-8'><title>Enter name</title></head><body>");
        out.println("<h2>Введите ваше имя</h2>");
        out.println("<form method='post' action='submit'>");
        out.println("<input type='text' name='name' required /> <br/><br/>");
        out.println("<button type='submit' name='vote' value='true'>Да</button>");
        out.println("<button type='submit' name='vote' value='false'>Нет</button>");
        out.println("</form>");
        out.println("</body></html>");
    }
}
//Староооеееее для веба
//Староооеееее для веба
//Староооеееее для веба
//Староооеееее для веба
//Староооеееее для веба
