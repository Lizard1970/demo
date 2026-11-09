package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;


    //用户名
    @NotBlank(message = "用户名不能为空！")
    private String username;

    //密码
    @NotBlank(message = "密码不能为空！")
    private String password;

    //年龄
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0！")
    private Integer age;
    private Integer userType;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private  LocalDateTime updateTime;



    // get、set
    //public LocalDateTime getCreatTime(){
    //   return createTime;
    //}

    //public void setCreatTime(LocalDateTime createTime){
     //   this.createTime = createTime;
    //}

    //public LocalDateTime getUpdateTime(){
     //   return updateTime;
    //}

    // public void setUpdateTime(LocalDateTime updateTime) {
    //    this.updateTime = updateTime;
    }
   //}