package com.mo.moment.entity.boardEntity;

import com.mo.moment.entity.albumEntity.AlbumImageEntity;
import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "board")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;
    @Column(length = 10000)
    private String content;
    @Column(name = "meta_date_time")
    private Date metaDateTime;
    @OneToMany(mappedBy = "boardEntity" , cascade = CascadeType.REMOVE , orphanRemoval = true , fetch = FetchType.LAZY)
    private List<AlbumImageEntity> imageEntities = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "kakao_id")
    private KakaoLoginEntity kakaoLoginEntity;

    public void setContent(String content) {
        this.content = content;
    }

    public void setMetaDateTime(Date metaDateTime) {
        this.metaDateTime = metaDateTime;
    }

    @Builder
    public BoardEntity (String content){
        this.content = content;
    }
}
