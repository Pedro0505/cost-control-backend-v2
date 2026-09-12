package pedro.cost.control.builders.entities;

import pedro.cost.control.domain.user.entities.User;

public class UserTestBuilder {

    private String username;
    private String email;
    private String password;

    public static UserTestBuilder builder() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public User build() {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();
    }
}