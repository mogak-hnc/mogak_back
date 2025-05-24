package com.hnc.mogak.member.adapter.out.persistence;

import com.hnc.mogak.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "image_path", length = 1024)
    private String imagePath;

    @Column(name = "name")
    private String name;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "withdrawn")
    private boolean withdrawn;

    @Column(name = "show_badge")
    private boolean showBadge;

}
