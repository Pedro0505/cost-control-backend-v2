package pedro.cost.control.helpers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pedro.cost.control.common.LegacyPageResponse;

import java.util.List;

public class LegacyPageResponseTestBuilder<T> {
    private List<T> content = List.of();
    private Pageable pageable = PageRequest.of(0, 10);
    private int totalPages = 1;
    private long totalElements = 0;
    private boolean last = true;
    private boolean first = true;
    private int size = 10;
    private int number = 0;
    private Sort sort = Sort.unsorted();
    private int numberOfElements = 0;
    private boolean empty = true;

    public static <T> LegacyPageResponseTestBuilder<T> builder() {
        return new LegacyPageResponseTestBuilder<>();
    }

    public LegacyPageResponseTestBuilder<T> withContent(List<T> content) {
        this.content = content;
        this.totalElements = content.size();
        this.numberOfElements = content.size();
        this.empty = content.isEmpty();
        return this;
    }

    public LegacyPageResponseTestBuilder<T> withPage(int number, int size) {
        this.number = number;
        this.size = size;
        this.pageable = PageRequest.of(number, size);
        return this;
    }

    public LegacyPageResponseTestBuilder<T> withTotalPages(int totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public LegacyPageResponseTestBuilder<T> withSort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public LegacyPageResponseTestBuilder<T> withFirst(boolean first) {
        this.first = first;
        return this;
    }

    public LegacyPageResponseTestBuilder<T> withLast(boolean last) {
        this.last = last;
        return this;
    }

    public LegacyPageResponse<T> build() {
        return new LegacyPageResponse<>(
                content,
                pageable,
                totalPages,
                totalElements,
                last,
                first,
                size,
                number,
                sort,
                numberOfElements,
                empty
        );
    }
}