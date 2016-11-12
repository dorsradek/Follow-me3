package pl.rdors.follow_me3.rest.model;

public class JwtAuthenticationResponse {

    private String token;
    private User user;

    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public User getUser() {
        return user;
    }
}