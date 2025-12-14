package com.rslakra.jwtauthentication4.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "vehicles")
public class Vehicle extends AbstractAuditableEntity<User, Long> implements Serializable {

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Brand brand = Brand.FORD;

}
