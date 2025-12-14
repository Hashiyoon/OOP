package org.example.client.app;


import org.example.client.api.ApiClient;
import org.example.client.api.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class Main {
    //Потом в конфиг засунуть!!!!
    private static final String SERVER_URL = System.getProperty("example.org", "http://localhost:8080/JavaP-BETAGAMMAALPHASUPERPREBUILDVERSION/");

    public void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApiClient apiClient = new ApiClient(SERVER_URL);
            MainFrame frame = new MainFrame(apiClient);
            frame.setVisible(true);
        });
    }

    class MainFrame extends JFrame {
        private final CardLayout cardLayout = new CardLayout();
        private final JPanel container = new JPanel(cardLayout);

        private final RegistrationFrame registrationFrame;
        private final ProfileFrame profileFrame;

        public static final String REG = "registration";
        public static final String PROFILE = "profile";

        public MainFrame(ApiClient apiClient) {
            super("Я устал");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(520, 350);
            setLocationRelativeTo(null);

            registrationFrame = new RegistrationFrame(apiClient, this::onRegisterSuccess);
            profileFrame = new ProfileFrame(apiClient, this::onLogout);

            container.add(registrationFrame, REG);
            container.add(profileFrame, PROFILE);
            setContentPane(container);

            cardLayout.show(container, REG);
        }

        private void onRegisterSuccess(Map<String, String> userData) {
            profileFrame.getUserProfile();
            cardLayout.show(container, PROFILE);
        }

        private void onLogout() {
            registrationFrame.clearForm();
            cardLayout.show(container, REG);
        }
    }

}
