package edu.miu.attendifypro.dto.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    private String message;
    private String code;
    private boolean status;
    private T data;

    private MessageErrorNode errors;
/*
    @JsonIgnore
    private HttpStatus httpStatusCode;
*/

    public ApiResponse(boolean status, T data) {
        this.status = status;
        this.data = data;
    }
    public ApiResponse(boolean status) {
        this.status = status;
    }

    public void setSuccess(T object, String message) {
        this.setStatus(true);
        this.setData(object);
        this.setMessage(message);
    }
    public void setSuccess(T object) {
        this.setStatus(true);
        this.setData(object);
    }

    public void setFailureValues( String message) {
        this.setStatus(false);
        this.setMessage(message);
    }

}