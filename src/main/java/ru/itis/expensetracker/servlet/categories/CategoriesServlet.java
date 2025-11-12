package ru.itis.expensetracker.servlet.categories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/categories")
public class CategoriesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CategoriesServlet.class);
    private CategoryService categoryService;

    @Override
    public void init() {
        categoryService = (CategoryService) getServletContext().getAttribute("categoryService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            List<Category> allCategories = categoryService.getAllCategoriesForUser(user.getId());
            
            List<Category> globalCategories = allCategories.stream()
                    .filter(c -> c.getUserId() == null)
                    .collect(Collectors.toList());
            
            List<Category> userCategories = allCategories.stream()
                    .filter(c -> c.getUserId() != null && c.getUserId().equals(user.getId()))
                    .collect(Collectors.toList());

            req.setAttribute("globalCategories", globalCategories);
            req.setAttribute("userCategories", userCategories);
            req.setAttribute("currentUserId", user.getId());

            req.getRequestDispatcher("/WEB-INF/jsp/categories/list.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Error loading categories", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при загрузке категорий.");
        }
    }
}

