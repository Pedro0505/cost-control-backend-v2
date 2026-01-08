package pedro.cost.control.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import org. springframework. data. domain. Pageable;
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
}