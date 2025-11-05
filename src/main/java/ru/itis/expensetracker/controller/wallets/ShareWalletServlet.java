package ru.itis.expensetracker.controller.wallets;

import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.WalletService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/wallets/share")
public class ShareWalletServlet extends HttpServlet {
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String emailToShare = req.getParameter("emailToShare");
        long walletId = Long.parseLong(req.getParameter("walletId"));
        User owner = (User) req.getSession().getAttribute("user");

        String redirectUrl = req.getContextPath() + "/expenses?walletId=" + walletId;

        try {
            walletService.shareWallet(walletId, owner.getId(), emailToShare);
            // Добавляем параметр успеха для отображения сообщения
            redirectUrl += "&share_success=true";
        } catch (ServiceException e) {
            // Добавляем параметр ошибки
            redirectUrl += "&share_error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

        resp.sendRedirect(redirectUrl);
    }
}