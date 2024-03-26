package com.mescobar.blog.dto.request;

import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchPostRequest {
    @QueryParam("author") String author;
    @QueryParam("title") String title;
    @QueryParam("dateFrom") String dateFrom;
    @QueryParam("dateTo") String dateTo;
}
