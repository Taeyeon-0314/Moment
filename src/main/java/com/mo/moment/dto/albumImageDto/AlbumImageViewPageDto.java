package com.mo.moment.dto.albumImageDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumImageViewPageDto {
    private List<AlbumImageViewRequestDto> content;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private boolean last;
}
