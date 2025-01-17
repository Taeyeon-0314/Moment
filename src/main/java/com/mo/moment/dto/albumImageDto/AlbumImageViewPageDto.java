package com.mo.moment.dto.albumImageDto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumImageViewPageDto {
    private List<AlbumImageViewRequestDto> content;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private boolean last;
}
