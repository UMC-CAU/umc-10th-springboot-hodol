package com.example.umc_study.domain.mission.entity;

import com.example.umc_study.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "location")
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Store> storeList = new ArrayList<>();
}
