package com.example.demo.service;

import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.vo.UserVO;


public interface UserService extends IService<User> {
    User addUser(User user);
    UserVO login(LoginDTO loginDTO);
    //模糊分页查询
    IPage<User> pageQuery(Page<User> page,String username);
}
