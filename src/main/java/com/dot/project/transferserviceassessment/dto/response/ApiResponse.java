package com.dot.project.transferserviceassessment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    private String responseCode;
    private String responseMsg;
    private String responseDesc;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("00","Success",null, data);
    }

    public static <T> ApiResponse<T> failed(T data) {
        return new ApiResponse<>("99","Failed",null, data);
    }

    public static <T> ApiResponse<T> error(String errorMsg) {
        return new ApiResponse<>("55","Failed", errorMsg, null);
    }
}
