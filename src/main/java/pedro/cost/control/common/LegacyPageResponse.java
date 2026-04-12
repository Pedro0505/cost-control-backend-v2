package pedro.cost.control.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Getter
@AllArgsConstructor
public class LegacyPageResponse<T> {
    private List<T> content;
    private Pageable pageable;
    private int totalPages;
    private long totalElements;
    private boolean last;
    private boolean first;
    private int size;
    private int number;
    private Sort sort;
    private int numberOfElements;
    private boolean empty;

    public LegacyPageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageable = page.getPageable();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.last = page.isLast();
        this.first = page.isFirst();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.sort = page.getSort();
        this.numberOfElements = page.getNumberOfElements();
        this.empty = page.isEmpty();
    }
}