package com.waitit.capstone.global.util;

import java.util.List;
import org.springframework.data.domain.Page;

public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public PageResponse(Page<T> pageData) {
        this.content = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalPages = pageData.getTotalPages();
        this.totalElements = pageData.getTotalElements();
    }
    }