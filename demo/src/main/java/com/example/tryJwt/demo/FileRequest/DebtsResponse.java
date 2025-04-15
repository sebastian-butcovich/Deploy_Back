package com.example.tryJwt.demo.FileRequest;
import com.example.tryJwt.demo.Modelo.Debts;

import java.util.List;

public record DebtsResponse(List<Debts> movents,  Integer next_page,
                            Integer page, Integer page_size, Integer total_entries, Integer total_pages,String mensaje) {
}