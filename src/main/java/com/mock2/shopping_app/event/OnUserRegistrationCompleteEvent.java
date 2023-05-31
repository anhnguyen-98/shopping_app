package com.mock2.shopping_app.event;

import com.mock2.shopping_app.model.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Setter
public class OnUserRegistrationCompleteEvent extends ApplicationEvent {
    private UriComponentsBuilder redirectUrl;
    private User user;

    public OnUserRegistrationCompleteEvent(UriComponentsBuilder redirectUrl, User user) {
        super(user);
        this.redirectUrl = redirectUrl;
        this.user = user;
    }
}
