package com.techbytedev.signboardmanager.dto.response;


import java.util.Date;

import com.techbytedev.signboardmanager.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponseDTO {
    private int id;
    private String title;
    private String content;
    private String excerpt;
    private String slug;
    private String imageBase64;
    private int categoryId;
    private int createdBy;
    private int updatedBy;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;



  
}

