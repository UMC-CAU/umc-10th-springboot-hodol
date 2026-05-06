package com.example.umc_study.domain.member.entity;

import com.example.umc_study.domain.member.entity.mapping.MemberTerm;
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
@Table(name = "term")
public class Term extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private Boolean optional;

    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberTerm> memberTermList = new ArrayList<>();
}
