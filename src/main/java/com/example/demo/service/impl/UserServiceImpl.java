package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //新增用户
    @Override
    public User addUser(User user) {
        //密码加密
        String encodePwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);
        boolean save = this.save(user);
        if (save) {
            return user;
        }
        return null;
    }
    //新增登录实现
    @Override
    public UserVO login(LoginDTO loginDTO){
        User user =this.lambdaQuery().eq(User::getUsername,loginDTO.getUsername()).one();
        //密码校验：matches(前端传明文，数据库加密！)
        if (user != null && passwordEncoder.matches(loginDTO.getPassword(),user.getPassword())){
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setAge(user.getAge());
            vo.setUserType(user.getUserType());
            vo.setCreateTime(user.getCreateTime());
            vo.setUpdateTime(user.getUpdateTime());
            return vo;
        }
        return null;
    }
    //用户查询
    @Override
    public IPage<User> pageQuery(Page<User> page, String username) {
        System.out.println("收到username参数"+username);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //username不为空的时候，才加模糊条件
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(User::getUsername,username.trim());

        }
        return this.page(page, wrapper);
    }
}
