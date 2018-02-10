package com.earthchen.elasticsearchspringbootstudy.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Novel {

    private String id;
    private String title;
    private String author;
    private Integer word_count;

    @DateTimeFormat( pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date public_date;
}
