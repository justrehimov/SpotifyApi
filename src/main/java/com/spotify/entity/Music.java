package com.spotify.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "artist_name", nullable = false, length = 60)
    private String artistName;

    @OneToOne
    @JoinColumn(name = "fk_storage_music", referencedColumnName = "id")
    private Storage music;

    @OneToOne
    @JoinColumn(name = "fk_storage_image", referencedColumnName = "id")
    private Storage image;

    @OneToOne
    @JoinColumn(name = "fk_user", referencedColumnName = "id")
    private User user;
}
