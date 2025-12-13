package org.example.client.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.example.client.api.ApiClient;
import org.example.client.api.ApiClient.ApiResponse;
import org.example.client.api.UserSession;

public class RegistrationFrame extends JPanel {
    private final ApiClient apiClient;
    private final Consumer<Map<String, String>> onSuccess; //Очень важная штука: она хранит ссылку на метод в экземпляре mainframe
    // когда мы вызываем метод accept() {в done() swingworker-а},
    // то получается вызываем функцию изнутри, словно поднимаемся из глубины воды за верёвку}

    private final JTextField loginField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton registerButton = new JButton("Log in");
    private final JButton passRestoreButton = new JButton("Забыли пароль?");
    private final JLabel status = new JLabel(" ");

    public RegistrationFrame(ApiClient apiClient, Consumer<Map<String, String>> onSuccess) {
        this.apiClient = apiClient;
        this.onSuccess = onSuccess;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.LINE_END;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Логин:"), g);
        g.gridx = 1; g.anchor = GridBagConstraints.LINE_START;
        form.add(loginField, g);

        g.gridx = 0; g.gridy = 1; g.anchor = GridBagConstraints.LINE_END;
        form.add(new JLabel("Пароль:"), g);
        g.gridx = 1; g.anchor = GridBagConstraints.LINE_START;
        form.add(passwordField, g);

        add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(registerButton);
        right.add(passRestoreButton);
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(right, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        registerButton.addActionListener(this::onRegisterClicked);
        passRestoreButton.addActionListener(this::passRestoreClicked);
    }

    private void setUiEnabled(boolean enabled) {
        loginField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        registerButton.setEnabled(enabled);
    }

    private void onRegisterClicked(ActionEvent e) {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "все поля заполнить", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setUiEnabled(false);
        status.setText("Вход...");

        SwingWorker<ApiResponse<Map<String, String>>, Void> worker = new SwingWorker<>() {
            private Exception error = null;

            @Override
            protected ApiResponse<Map<String, String>> doInBackground() {
                try {
                    return apiClient.register(login, password); //Подумать над вовращ. типом данн
                } catch (Exception ex) {
                    this.error = ex;
                    return null;
                }
            }
//ИИИИИИ
            @Override
            protected void done() {
                setUiEnabled(true);
                try {
                    if (error != null) throw error;

                    ApiResponse<Map<String,String>> resp = get();
                    if (resp == null) {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Empty response from client", "Error", JOptionPane.ERROR_MESSAGE);
                        status.setText("Error");
                        return;
                    }

                    if (resp.ok) {
                        Map<String,String> data = resp.body != null ? resp.body : Map.of();
                        String login = data.get("login");

                        //Здесь создаётся инфа по сессии. api чётко будет знать пользователя
                        UserSession.get().setLogin(login);
                        UserSession.get().setProfile(new HashMap<>(data));

                        status.setText("OK");
                        onSuccess.accept(data); // тут даём знак MainFrame, что мы зарегались и го некст
                    } else {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Registration failed: " + resp.error,
                                "Error", JOptionPane.ERROR_MESSAGE);
                        status.setText("Error");
                    }
                } catch (Exception ex) { // ExecutionException, InterruptedException или наше error
                    String msg = ex.getMessage() != null ? ex.getMessage() : ex.toString();
                    JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "Unexpected error: " + msg,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    status.setText("Error");
                }
            }
//ИИИИИИ
        };

        worker.execute();
    }

    private void passRestoreClicked(ActionEvent e) {
        JTextField loginRField = new JTextField(25);
        JTextField passwordRField = new JTextField(25);
        Object[] content = {
                "Введите логин и пароль для восстановления пароля:", loginRField, passwordRField
        };
        int option = JOptionPane.showConfirmDialog(this, content, "Восстановление пароля", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        String login = loginRField.getText().trim();
        String password = passwordRField.getText().trim();
        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните поля", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setUiEnabled(false);
        status.setText("Запрос восстановения...");

        SwingWorker<ApiResponse<Map<String, String>>, Void> worker = new SwingWorker<>() {
            private Exception error = null;

            @Override
            protected ApiResponse<Map<String, String>> doInBackground() {
                try {
                    return apiClient.restorePass(login, password); //Всё ещё подумать над вовращ. типом данн
                } catch (Exception ex) {
                    this.error = ex;
                    return null;
                }
            }
            @Override
            protected void done() {
                setUiEnabled(true);
                try {
                    if (error != null) throw error;

                    ApiResponse<Map<String,String>> resp = get();
                    if (resp == null) {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Если такой пользователь был, то пароль точно поменялся ;)", "Error", JOptionPane.ERROR_MESSAGE);
                        status.setText("Error");
                        return;
                    }

                    if (resp.ok) {
                        status.setText("Пароль успешно изменён");
                    } else {
                        JOptionPane.showMessageDialog(RegistrationFrame.this,
                                "Ошибка восстановления failed: " + resp.error,
                                "Error", JOptionPane.ERROR_MESSAGE);
                        status.setText("Error");
                    }
                } catch (Exception ex) { // ExecutionException, InterruptedException или наше error
                    String msg = ex.getMessage() != null ? ex.getMessage() : ex.toString();
                    JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "Unexpected error: " + msg,
                            "Error", JOptionPane.ERROR_MESSAGE);
                    status.setText("Error");
                }
            }
        };

        worker.execute();
    }

    public void clearForm() {
        loginField.setText("");
        passwordField.setText("");
        status.setText(" ");
    }

}
