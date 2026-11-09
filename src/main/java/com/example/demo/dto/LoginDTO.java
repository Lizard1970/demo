package com.example.demo.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空！")
    private String username;

    @NotBlank(message = "账号密码不能为空！")
    private  String password;
}
