package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Result;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.metadata.IPage;


@CrossOrigin(origins = "*")
@RestController
//@RequestMapping("/demo")
public class Controller {
    @Resource
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired PasswordEncoder passwordEncoder;

    // 查询用户--分页查询
    @GetMapping("/user/page")
    public Result<Object> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) String sortFile,
            @RequestParam(required = false) String sortOrder
        ){
        Page<User> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        //用户名不为空、不是空白字符串才能模糊查询
        wrapper.like(username !=null && !username.isBlank(), User::getUsername,username);
        //userType不为null才筛选
        wrapper.eq(userType !=null,User::getUserType,userType);

        if (sortFile == null || sortFile.isBlank()) {
            wrapper.orderByDesc(User::getCreateTime);
        }else {
            boolean isAsc = "asc".equals(sortOrder);
            if ("createTime".equals(sortFile)){
                wrapper.orderBy(true, isAsc, User::getCreateTime);
            }else if ("age".equals(sortFile)){
                wrapper.orderBy(true,isAsc,User::getAge);
            }else {
                //传了非法字段，兜底
                wrapper.orderByDesc(User::getCreateTime);
            }
        }
        IPage<User> res  = userService.page(page,wrapper);
        return Result.success(res);
         }

         //查询用户--根据id
    @GetMapping("/user/{id}")
    public Result<User> getUserId(@PathVariable Long id) {
        User dbUser = userService.getById(id);
        return Result.success(dbUser);
    }


    //新增用户
    @PostMapping("/user/add")
    public Result<Object> addUser(@Valid @RequestBody User user){
        User newUser = userService.addUser(user);
        if (newUser != null){
            return Result.success("新增成功："+newUser);
        }
        return Result.fail("新增失败!");
    }
    // 修改用户信息接口
    @PutMapping("/user/update")
    public Result<Object> updateUser(@RequestBody User user){
        //先查数据库的数据
        User dbUser = userService.getById(user.getId());
        if (dbUser == null){
            return  Result.fail("用户不存在");
        }
        //如果前端传来的密码是空的，不修改旧密码
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()){
            user.setPassword(null);

        }else {
            //有新密码，加密
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        boolean b = userService.updateById(user);
        if (b){
            return Result.success("修改成功！");
        }
        return Result.fail("修改失败！");
    }

    //删除用户接口
    @DeleteMapping("/user/del/{id}")
    public Result<Object> delUser(@PathVariable Long id){
        boolean b = userService.removeById(id);
        if (b){
            return Result.success("删除成功！");
        }
        return Result.fail("删除失败！");
    }

    //登录接口
    @PostMapping("/user/login")
    public Result<String> login(@RequestBody User user) {
        // 去数据库查该用户
        User dbUser = userService.lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .one();
                System.out.print("数据库查到用户："+dbUser);
        if (dbUser == null) {
            return Result.fail("账号不存在");
        }
        //对比密码
        if (!passwordEncoder.matches(user.getPassword(),dbUser.getPassword())) {
            return Result.fail("密码错误！");
        }
        String token = jwtUtil.createToken(dbUser.getId());
        return Result.success(token);
    }
}





