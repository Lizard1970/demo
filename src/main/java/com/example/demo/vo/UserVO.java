package com.example.demo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private Integer age;
    private Integer userType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
