package com.spotify.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "unique_name")
    private String uniqueName;

    @Column(name = "local_path")
    private String localPath;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "extension")
    private String extension;

    @Column(name = "url")
    private String url;

    @Column(name = "size")
    private Long size;
}
