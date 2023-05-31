package com.mock2.shopping_app.event;

import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.token.EmailVerificationToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class OnResendEmailVerificationEvent extends ApplicationEvent {
    private UriComponentsBuilder redirectUrl;
    private User user;
    private EmailVerificationToken token;

    public OnResendEmailVerificationEvent(UriComponentsBuilder redirectUrl, User user,
                                          EmailVerificationToken token) {
        super(user);
        this.redirectUrl = redirectUrl;
        this.user = user;
        this.token = token;
    }
}
