package org.example.client.app;

import org.example.api.ApiClient;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Main {
    //Потом в конфиг засунуть!!!!
    private static final String SERVER_URL = System.getProperty("example.org", "http://localhost:8080/JavaP-BETAGAMMAALPHASUPERPREBUILDVERSION/");


    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApiClient apiClient = new ApiClient(SERVER_URL);
            MainFrame frame = new MainFrame(apiClient);
            frame.setVisible(true);
        });
    }
}

class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);

    private final RegistrationFrame registrationFrame;
    private final OrdersFrame ordersFrame;
    private final JTabbedPane tabbedPane;

    public static final String REG = "registration";
    public static final String MAIN = "main";


    public MainFrame(ApiClient apiClient) {
        super("Система управления заказами");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        registrationFrame = new RegistrationFrame(apiClient, this::onRegisterSuccess);

        // Создаем основную панель с вкладками
        tabbedPane = new JTabbedPane();
        ordersFrame = new OrdersFrame(apiClient, this::onLogout);

        tabbedPane.addTab("Профиль", ordersFrame);
        tabbedPane.addTab("Мои заказы", ordersFrame);

        container.add(registrationFrame, REG);
        container.add(tabbedPane, MAIN);
        setContentPane(container);

        cardLayout.show(container, REG);
    }

    private void onRegisterSuccess(Map<String, String> userData) {
        ordersFrame.refreshOrders(null); // Загружаем заказы при успешной авторизации
        //Пытаемся вызвать приватную функцию - невозможно
        cardLayout.show(container, MAIN);
    }

    private void onLogout(Object a) {
        UserSession.get().clear(); // Очищаем сессию
        registrationFrame.clearForm();
        cardLayout.show(container, REG);
    }
}