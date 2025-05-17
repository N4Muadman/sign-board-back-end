package com.techbytedev.signboardmanager.dto.response;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultPaginationDTO {

    private List<?> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}