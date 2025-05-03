package zercher.be.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> implements Serializable {
    private Boolean success;

    private String error;

    private T data;

    public BaseResponse(Boolean success) {
        this.success = success;
        this.error = null;
        this.data = null;
    }

    public BaseResponse(String error) {
        this.success = false;
        this.error = error;
        this.data = null;
    }

    public BaseResponse(T data) {
        this.success = true;
        this.error = null;
        this.data = data;
    }
}
