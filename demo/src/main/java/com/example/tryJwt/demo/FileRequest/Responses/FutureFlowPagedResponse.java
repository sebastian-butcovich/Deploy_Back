package com.example.tryJwt.demo.FileRequest.Responses;
import com.example.tryJwt.demo.Modelo.FutureFlow;

import java.util.List;

public record FutureFlowPagedResponse(List<FutureFlow> movements, Integer next_page,
                                      Integer page, Integer page_size, Integer total_entries, Integer total_pages, String mensaje) {
}