package org.example.client.api;

import java.util.Map;

public class UserSession {
    private static final UserSession INSTANCE = new UserSession();
    private String login;
    private Map<String,String> profile;

    private UserSession() {}

    public static UserSession get() { return INSTANCE; }

    public void setLogin(String login) { this.login = login; }
    public void setProfile(Map<String,String> profile) { this.profile = profile; }


    public String getLogin() { return login; }
    public Map<String,String> getProfile() { return profile; }

    public void clear() {
        login = null; profile = null;
    }
}
