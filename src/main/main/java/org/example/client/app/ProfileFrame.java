package org.example.client.app;

import org.example.client.api.ApiClient;
import org.example.client.api.ApiClient.ApiResponse;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


public class ProfileFrame extends JPanel {
    private final JLabel lblLogin = new JLabel();
    /*
    Кнопки и поля - всякая мишура
     */

    private final Runnable onLogout; //ссылка на метод MainFrame для выхода
    private final ApiClient apiClient;

    private final JButton logoutBtn = new JButton("Logout");
    private final JButton refreshBtn = new JButton("Обновить");

    public ProfileFrame(ApiClient apiClient, Runnable onLogout) {
        this.apiClient = apiClient;
        this.onLogout = onLogout;
        initUI();

    }

    private void initUI() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Профиль пользователя");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);


        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.LINE_START;

        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Логин:"), g);
        g.gridx = 1;
        panel.add(lblLogin, g);

//        Пример, какое ещё добавить окошко с данными
//        g.gridx = 0; g.gridy = 2;
//        panel.add(new JLabel("Email:"), g);
//        g.gridx = 1;
//        panel.add(lblEmail, g);
        add(panel, BorderLayout.CENTER);


        JPanel panelbtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelbtn.add(refreshBtn);
        panelbtn.add(logoutBtn);

        logoutBtn.addActionListener(e -> onLogout.run());
        refreshBtn.addActionListener(e -> getUserProfile());

    }

    public void getUserProfile() {
        SwingWorker<ApiResponse<Map<String, String>>, Void> worker =
                new SwingWorker<>() {
                    @Override
                    protected ApiResponse<Map<String, String>> doInBackground() throws Exception {
                        return apiClient.profile();//написать сервлет, чтобы всё работало
                    }

                    @Override
                    protected void done() {
                        try {
                            var resp = get();
                            if (resp.ok) {//пока что доходит досюда и уходит в else, т.к. сервер не обрабатывает /profile
                                updateFrame(resp.body);
                            } else {
                                JOptionPane.showMessageDialog(ProfileFrame.this,
                                        resp.error, "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ProfileFrame.this,
                                    ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
        worker.execute();
    }

    public void updateFrame(Map<String, String> map) {
        /*
        Данные, которые обновить
         */
        String login = map.getOrDefault("login", "");//считать с преобразованного ответа сервера
        lblLogin.setText(login);//в окошко вставить
        // И т.д.

    }

    public void clear() {
        lblLogin.setText("");
        /*
        И тут все данные очистить отдельно
         */
    }
}
