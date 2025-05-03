package com.cuentasbp.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {

    private String status;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public static <T> ResponseDTO<T> success(T data) {
        return ResponseDTO.<T>builder()
                .status("success")
                .message("Operación completada con éxito")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return ResponseDTO.<T>builder()
                .status("success")
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ResponseDTO<T> error(String message) {
        return ResponseDTO.<T>builder()
                .status("error")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static <T> ResponseDTO<T> error(String message, T data) {
        return ResponseDTO.<T>builder()
                .status("success")
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }
}
