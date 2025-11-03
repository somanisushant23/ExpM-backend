package com.userservice.expmbff.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long createdOn;

    private Long updatedOn;

    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis();
        this.createdOn = now;
        this.updatedOn = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = System.currentTimeMillis();
    }
}
