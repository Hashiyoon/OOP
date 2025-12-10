package org.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SwingMain {
    //Потом в конфиг засунуть!!!!
    private static final String SERVER_URL = System.getProperty("example.org", "http://localhost:8080/JavaP-BETAGAMMAALPHASUPERPREBUILDVERSION/submit");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingMain::createAndShow);
    }

    private static void createAndShow() {
        JFrame frame = new JFrame("Vote");
        JTextField nameField = new JTextField(20);
        JButton yes = new JButton("Да");
        JButton no = new JButton("Нет");
        JLabel status = new JLabel(" ");

        yes.addActionListener((ActionEvent e) -> send(nameField.getText(), true, status));
        no.addActionListener((ActionEvent e) -> send(nameField.getText(), false, status));

        JPanel p = new JPanel();
        p.add(new JLabel("Имя:"));
        p.add(nameField);
        p.add(yes);
        p.add(no);
        p.add(status);

        frame.setContentPane(p);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void send(String name, boolean vote, JLabel status) {
        if (name == null || name.trim().isEmpty()) {
            status.setText("Введите имя");
            return;
        }
        new SwingWorker<Void, Void>() {
            @Override
            //а вот и api😈😈😈
            protected Void doInBackground() throws Exception {
                String form = "name=" + URLEncoder.encode(name, StandardCharsets.UTF_8)
                        + "&vote=" + URLEncoder.encode(Boolean.toString(vote), StandardCharsets.UTF_8);

                HttpClient client = HttpClient.newBuilder()
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_URL))
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(form))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int code = response.statusCode();
                String body = response.body();

                if (code == 200) {
                    SwingUtilities.invokeLater(() -> status.setText("Отправлено"));
                } else {
                    SwingUtilities.invokeLater(() -> status.setText("Ошибка " + code + ": " + body));
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    status.setText("Ошибка: " + ex.getMessage());
                }
            }
        }.execute();
    }
}