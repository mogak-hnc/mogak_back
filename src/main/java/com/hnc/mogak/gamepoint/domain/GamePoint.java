package com.hnc.mogak.gamepoint.domain;

import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GamePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(nullable = false)
    private int balance;

    public static GamePoint create(Member member) {
        return new GamePoint(null, MemberMapper.mapToJpaEntity(member), 0);
    }

    public void addPoint(Integer amount) {
        this.balance += amount;
    }

}