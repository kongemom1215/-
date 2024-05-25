package com.unity.potato.domain.board.free;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FreeBoardReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 18)
    private String userName;

    @Column(name = "content", nullable = false, length = 400)
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

}
