package zercher.be.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> extends BaseResponse<List<T>> implements Serializable {
    private Integer pageSize;
    private Integer pageNumber;
    private Long totalElements;

    public PageResponse(Page<T> page) {
        this.setData(page.getContent());
        this.setTotalElements(page.getTotalElements());
        this.setPageNumber(page.getPageable().getPageNumber());
        this.setPageSize(page.getPageable().getPageSize());
        this.setSuccess(true);
        this.setError(null);
    }
}
