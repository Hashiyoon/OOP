package org.example.client.app;

import org.example.api.ApiClient;
import org.example.api.ApiClient.ApiResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

public class OrdersFrame extends JPanel {
    private final Consumer onLogout;
    private final ApiClient apiClient;

    private final DefaultTableModel tableModel;
    private final JTable ordersTable;
    private final JButton refreshBtn;
    private final JButton createOrderBtn;
    private final JButton updateStatusBtn;
    private final JButton logoutBtn;

    private final String[] COLUMN_NAMES = {"ID заказа", "Товар", "Количество", "Цена", "Статус", "Дата создания"};

    public OrdersFrame(ApiClient apiClient, Consumer onLogout) {
        this.apiClient = apiClient;
        this.onLogout = onLogout;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Заголовок
        JLabel title = new JLabel("Мои заказы");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        // Таблица заказов
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        refreshBtn = new JButton("Обновить");
        createOrderBtn = new JButton("Создать заказ");
        updateStatusBtn = new JButton("Изменить статус");
        logoutBtn = new JButton("Log out");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(createOrderBtn);
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий
        refreshBtn.addActionListener(this::refreshOrders);
        createOrderBtn.addActionListener(this::createOrder);
        updateStatusBtn.addActionListener(this::updateOrderStatus);
        logoutBtn.addActionListener(this::logout);
        // Загружаем заказы при создании    Upd: мы создаём при инициализации окна mainframe, поэтому вызов бесполезен и опасен
//        refreshOrders(null);
    }
    //Было private
    public void refreshOrders(ActionEvent e) {
        SwingWorker<ApiResponse<String>, Void> worker = new SwingWorker<>() {

            @Override
            protected ApiResponse<String> doInBackground() throws Exception {
                return apiClient.getUserOrders();
            }

            @Override
            protected void done() {
                try {
                    ApiResponse<String> resp = get();
                    if (resp.ok) {
                        updateTable(resp.body);
                    } else {
                        JOptionPane.showMessageDialog(OrdersFrame.this,
                                "Ошибка при загрузке заказов: " + resp.error,
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(OrdersFrame.this,
                            "Ошибка: " + ex.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void logout(ActionEvent a) {
        onLogout.accept(null);
    }

    private void updateTable(String ordersData) {
        tableModel.setRowCount(0);

        if (ordersData == null || ordersData.isEmpty() || ordersData.equals("Заказов не найдено")) {
            return;
        }

        String[] orders = ordersData.split("\n");
        for (String order : orders) {
            Map<String, String> orderMap = parseOrderString(order);
            if (!orderMap.isEmpty()) {
                tableModel.addRow(new Object[]{
                        orderMap.get("order_id"),
                        orderMap.get("product_name"),
                        orderMap.get("quantity"),
                        orderMap.get("price") + " руб.",
                        orderMap.get("status"),
                        orderMap.get("created_at")
                });
            }
        }
    }

    private Map<String, String> parseOrderString(String orderStr) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = orderStr.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    private void createOrder(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        JTextField productField = new JTextField(20);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JTextField priceField = new JTextField(10);

        panel.add(new JLabel("Товар:"));
        panel.add(productField);
        panel.add(new JLabel("Количество:"));
        panel.add(quantitySpinner);
        panel.add(new JLabel("Цена за единицу:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Создание заказа",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String productName = productField.getText().trim();
            String priceText = priceField.getText().trim();

            if (productName.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int quantity = (Integer) quantitySpinner.getValue();
                double price = Double.parseDouble(priceText);

                SwingWorker<ApiResponse<Map<String, String>>, Void> worker = new SwingWorker<>() {
                    @Override
                    protected ApiResponse<Map<String, String>> doInBackground() throws Exception {
                        return apiClient.createOrder(productName, quantity, price);
                    }

                    @Override
                    protected void done() {
                        try {
                            ApiResponse<Map<String, String>> resp = get();
                            if (resp.ok) {
                                JOptionPane.showMessageDialog(OrdersFrame.this,
                                        "Заказ создан успешно! ID: " + resp.body.get("order_id"),
                                        "Успех", JOptionPane.INFORMATION_MESSAGE);
                                refreshOrders(null);
                            } else {
                                JOptionPane.showMessageDialog(OrdersFrame.this,
                                        "Ошибка при создании заказа: " + resp.error,
                                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(OrdersFrame.this,
                                    "Ошибка: " + ex.getMessage(),
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Неверный формат цены",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateOrderStatus(ActionEvent e) {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите заказ для изменения статуса",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orderId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        String[] statuses = {"pending", "processing", "completed", "cancelled"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
                "Выберите новый статус для заказа " + orderId + ":\nТекущий статус: " + currentStatus,
                "Изменение статуса", JOptionPane.PLAIN_MESSAGE, null, statuses, currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            SwingWorker<ApiResponse<Map<String, String>>, Void> worker = new SwingWorker<>() {
                @Override
                protected ApiResponse<Map<String, String>> doInBackground() throws Exception {
                    return apiClient.updateOrderStatus(orderId, newStatus);
                }

                @Override
                protected void done() {
                    try {
                        ApiResponse<Map<String, String>> resp = get();
                        if (resp.ok) {
                            JOptionPane.showMessageDialog(OrdersFrame.this,
                                    "Статус заказа обновлен",
                                    "Успех", JOptionPane.INFORMATION_MESSAGE);
                            refreshOrders(null);
                        } else {
                            JOptionPane.showMessageDialog(OrdersFrame.this,
                                    "Ошибка при обновлении статуса: " + resp.error,
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(OrdersFrame.this,
                                "Ошибка: " + ex.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
