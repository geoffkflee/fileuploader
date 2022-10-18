package com.geoffkflee.fileloader.fileloaderapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class GenericEntity {


    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    UUID id;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    Instant createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    Instant lastModifiedAt;

}
