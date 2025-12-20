package org.example.api;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLDecoder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.client.app.UserSession;
import org.example.server.dao.DTO.*;

public class ApiClient {
    private final String url;
    private final HttpClient client;


    public ApiClient(String url) {
        this.url = url.endsWith("/") ? url.substring(0, url.length()-1) : url;
        this.client = HttpClient.newBuilder().build();
    };

    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private HttpResponse<String> postForm(String path, Map<String,String> form) throws Exception {
        String posturl = form
                .entrySet()
                .stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(posturl, StandardCharsets.UTF_8))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getForm(String path, Map<String,String> form) throws Exception {
        String geturl = form
                .entrySet()
                .stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + path + "?" + geturl))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private ApiResponse<Map<String, String>> parseResponse(HttpResponse<String> resp) {
        int code = resp.statusCode();
        String body = resp.body();

        if (code >= 200 && code < 300) {
            try {
                Map<String, String> data = parseFormBody(body);
                return ApiResponse.success(code, data);
            } catch (Exception ex) {
                return ApiResponse.error(code, "Ошибка при обработке ответа: " + ex.getMessage());
            }
        }
        else {
            return ApiResponse.error(code, body);
        }
    }

    private Map<String, String> parseFormBody(String body) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();

        if (body == null || body.isEmpty())
            return map;

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String val = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            map.put(key, val);
        }

        return map;
    }

    public static class ApiResponse<T> {
        public final boolean ok;
        public final int code;
        public final T body;
        public final String error;

        private ApiResponse(boolean ok, int code, T body, String error) {
            this.ok = ok;
            this.code = code;
            this.body = body;
            this.error = error;
        }
        public static <T> ApiResponse<T> success(int code, T body) {return new ApiResponse<>(true, code, body, null);}
        public static <T> ApiResponse<T> error(int code, String error) {return new ApiResponse<>(false, code, null, error);}
    }




    public ApiResponse<Map<String, String>> register(String login, String password) throws Exception {
        RegisterRequest req = new RegisterRequest(login, password);
        HttpResponse<String> resp = postForm("/register", req.toMap());
        return parseResponse(resp);
    }

    public ApiResponse<Map<String, String>> restorePass(String login, String password) throws Exception {
        RestoreRequest req = new RestoreRequest(login, password);
        HttpResponse<String> resp = postForm("/restore", req.toMap());
        return parseResponse(resp);
    }

    public ApiResponse<Map<String, String>> createOrder(String productName, int quantity, double price) throws Exception {
        CreateUserOrdersRequest req = new CreateUserOrdersRequest(
                "create",
                UserSession.get().getLogin(),
                productName,
                String.valueOf(quantity),
                String.valueOf(price)
        );
        HttpResponse<String> resp = postForm("/orders", req.toMap());
        return parseResponse(resp);
    }

    public ApiResponse<String> getUserOrders() throws Exception {
        GetUserOrdersRequest req = new GetUserOrdersRequest(
                UserSession.get().getLogin()
        );

        HttpResponse<String> resp = getForm("/orders", req.toMap());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return ApiResponse.success(resp.statusCode(), resp.body());
        } else {
            return ApiResponse.error(resp.statusCode(), resp.body());
        }
    }

    public ApiResponse<Map<String, String>> updateOrderStatus(String orderId, String status) throws Exception {
        UpdateUserOrdersRequest req = new UpdateUserOrdersRequest(
                "update_status",
                orderId,
                status
        );
        HttpResponse<String> resp = postForm("/orders", req.toMap());
        return parseResponse(resp);
    }
}
