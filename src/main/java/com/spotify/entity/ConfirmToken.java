package com.spotify.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfirmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "email")
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToOne
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;


    public ConfirmToken(User user){
        this.user = user;
        this.expiredAt = Date.from(Instant.now().plusSeconds(600));
        this.createdAt = Date.from(Instant.now());
        this.token = UUID.randomUUID().toString();
        this.email = user.getEmail();
    }

    public ConfirmToken(User user, String email){
        this.user = user;
        this.expiredAt = Date.from(Instant.now().plusSeconds(600));
        this.createdAt = Date.from(Instant.now());
        this.token = UUID.randomUUID().toString();
        this.email = email;
    }
}
