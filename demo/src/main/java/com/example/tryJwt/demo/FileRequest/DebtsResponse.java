package com.example.tryJwt.demo.FileRequest;
import com.example.tryJwt.demo.Modelo.FutureFlows;

import java.util.List;

public record DebtsResponse(List<FutureFlows> movents, Integer next_page,
                            Integer page, Integer page_size, Integer total_entries, Integer total_pages, String mensaje) {
}