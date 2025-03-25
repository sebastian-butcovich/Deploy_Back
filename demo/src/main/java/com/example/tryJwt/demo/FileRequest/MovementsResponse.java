package com.example.tryJwt.demo.FileRequest;

import java.util.List;

public record MovementsResponse(List<MovementsRequest> movents,Additional_info additionalInfo,Integer next_page,
                                Integer page,Integer page_size,Integer total_entries,Integer total_pages) {
}
