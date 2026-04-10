package com.example.tryJwt.demo.FileRequest.Responses;

import com.example.tryJwt.demo.FileRequest.AdditionalInfo;
import com.example.tryJwt.demo.FileRequest.Request.MovementsRequest;

import java.util.List;

public record MovementsPagedResponse(List<MovementsRequest> movements, AdditionalInfo additionalInfo, Integer next_page,
                                     Integer page, Integer page_size, Integer total_entries, Integer total_pages) {
}
