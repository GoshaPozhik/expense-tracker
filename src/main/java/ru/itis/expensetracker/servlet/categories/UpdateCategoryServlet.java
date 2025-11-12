package ru.itis.expensetracker.servlet.categories;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.CategoryService;

@WebServlet("/categories/edit")
public class UpdateCategoryServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCategoryServlet.class);
    private CategoryService categoryService;

    @Override
    public void init() {
        categoryService = (CategoryService) getServletContext().getAttribute("categoryService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("id");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (idParam == null || idParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID категории не указан.");
                return;
            }

            long categoryId = Long.parseLong(idParam);
            Category category = categoryService.getCategoryById(categoryId, user.getId());

            req.setAttribute("category", category);
            req.getRequestDispatcher("/WEB-INF/jsp/categories/edit.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID категории.");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/categories?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error loading category for edit", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при загрузке категории.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("categoryId");
            String name = req.getParameter("name");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (idParam == null || idParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID категории не указан.");
                return;
            }

            long categoryId = Long.parseLong(idParam);
            categoryService.updateCategory(categoryId, name, user.getId());

            resp.sendRedirect(req.getContextPath() + "/categories?success=" +
                    URLEncoder.encode("Категория успешно обновлена.", StandardCharsets.UTF_8));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID категории.");
        } catch (ServiceException e) {
            logger.warn("Error updating category: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/categories?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Unexpected error updating category", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при обновлении категории.");
        }
    }
}

