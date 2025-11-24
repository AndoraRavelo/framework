package com.example; 
 
import jakarta.servlet.http.*; 
import jakarta.servlet.annotation.WebServlet; 
 
@WebServlet("/example") 
public class ExampleServlet extends HttpServlet { 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) { 
        // Code du servlet 
    } 
} 
