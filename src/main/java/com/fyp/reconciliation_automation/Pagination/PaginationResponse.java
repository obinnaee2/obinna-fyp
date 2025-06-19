package com.fyp.reconciliation_automation.Pagination;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PaginationResponse {
    private int pageSize;
    private int pageNumber;
    private long totalNumberOfItems;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;

    @Override
    public String toString() {
        return "PaginationResponse{" +
                "pageSize=" + pageSize +
                ", pageNumber=" + pageNumber +
                ", totalNumberOfItems=" + totalNumberOfItems +
                ", totalPages=" + totalPages +
                ", isFirst=" + isFirst +
                ", isLast=" + isLast +
                '}';
    }
}