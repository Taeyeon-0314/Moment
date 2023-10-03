package com.mo.moment.entity.boardEntity;

import com.mo.moment.entity.albumEntity.AlbumImageEntity;
import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import com.mo.moment.jwt.entity.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "board")
public class BoardEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;
    @Column(length = 10000)
    private String content;
    @OneToMany(mappedBy = "boardEntity" , cascade = CascadeType.REMOVE , orphanRemoval = true , fetch = FetchType.LAZY)
    private List<AlbumImageEntity> imageEntities = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "kakao_id")
    private KakaoLoginEntity kakaoLoginEntity;

    public void setContent(String content) {
        this.content = content;
    }

    @Builder
    public BoardEntity (String content){
        this.content = content;
    }
}
