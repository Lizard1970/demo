package com.example.demo.config;
import com.example.demo.entity.Result;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleValidError(MethodArgumentNotValidException e){
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError.getDefaultMessage();
        return Result.fail(msg);
    }
    @ExceptionHandler(Exception.class)
    public Result<Object> handleAll(Exception e){
        e.printStackTrace();
        return Result.fail("异常："+e.getMessage());
    }


}
