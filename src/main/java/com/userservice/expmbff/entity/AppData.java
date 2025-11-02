package com.userservice.expmbff.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "app_metadata_info")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppData extends BaseModelEntity {
    private String osName;

    private Float minVersion;

    private Float latestVersion;
}
