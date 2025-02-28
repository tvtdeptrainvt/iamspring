package com.example.database.exception;


import com.example.database.dto.request.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ApiResponse apiResponse = new ApiResponse();
        if (ex.getRequestURL().contains("abc")) {
            apiResponse.setCode(ErrorCode.NOT_FOUND.getCode());
            apiResponse.setMessages("UserEntity not found");
        } else {
            apiResponse.setCode(ErrorCode.NOT_FOUND.getCode());
            apiResponse.setMessages("Endpoint not found: " + ex.getRequestURL());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handLingRuntimeException(RuntimeException exception){
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessages(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handLingAppException(AppException exception){
        ErrorCode errorcode = exception.getErrorcode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorcode.getCode());
        apiResponse.setMessages(errorcode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handValidation(MethodArgumentNotValidException exception){
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorcode = ErrorCode.INVALID_KEY;
        try {
            errorcode = ErrorCode.valueOf(enumKey);
        }catch (IllegalArgumentException e){

        }

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorcode.getCode());
        apiResponse.setMessages(errorcode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
}  // khi tra check 1 duong dan ko co y nghia se tra ve trang co y nghia
// vidu : khi search */abc se tra ve " userEntity not fond"
