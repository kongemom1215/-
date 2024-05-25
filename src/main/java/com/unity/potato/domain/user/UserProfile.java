package com.unity.potato.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    @Column(name = "user_profile_img")
    @Lob @Basic(fetch = FetchType.EAGER)
    private String userProfileImg;

    @Column(name = "user_profile_introduce")
    private String bio;

    public UserProfile(UserInfo userInfo){
        this.userInfo = userInfo;
    }
}
