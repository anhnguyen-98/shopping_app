package com.mock2.shopping_app.model.token;

import com.mock2.shopping_app.model.audit.DateAudit;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "email_verification_token")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmailVerificationToken extends DateAudit {

    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "token_status")
    @Enumerated(EnumType.STRING)
    private TokenStatus tokenStatus;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    public void setConfirmedStatus() {
        this.tokenStatus = TokenStatus.STATUS_CONFIRMED;
    }
}
