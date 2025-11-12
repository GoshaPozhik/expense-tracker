package ru.itis.expensetracker.servlet.categories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.CategoryService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/categories/add")
public class AddCategoryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AddCategoryServlet.class);
    private CategoryService categoryService;

    @Override
    public void init() {
        categoryService = (CategoryService) getServletContext().getAttribute("categoryService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String name = req.getParameter("name");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            categoryService.createCategory(name, user.getId());
            resp.sendRedirect(req.getContextPath() + "/categories?success=" + 
                    URLEncoder.encode("Категория успешно создана.", StandardCharsets.UTF_8));
        } catch (ServiceException e) {
            logger.warn("Error creating category: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/categories?error=" + 
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Unexpected error creating category", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при создании категории.");
        }
    }
}

