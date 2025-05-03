package com.example.tryJwt.demo.FileRequest.Paginated;

public class InfoPaginated {
    private Integer next_page;
    private Integer page;
    private Integer page_size;
    private Integer total_entries;
    private Integer total_pages;

    public Integer getNext_page() {
        return next_page;
    }

    public void setNext_page(Integer next_page) {
        this.next_page = next_page;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }

    public Integer getTotal_entries() {
        return total_entries;
    }

    public void setTotal_entries(Integer total_entries) {
        this.total_entries = total_entries;
    }

    public Integer getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(Integer total_pages) {
        this.total_pages = total_pages;
    }
}
