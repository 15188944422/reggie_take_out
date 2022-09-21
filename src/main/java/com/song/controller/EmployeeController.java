package com.song.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.song.common.R;
import com.song.pojo.Employee;
import com.song.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
//处理请求的路径 是先看前端 发送请求 看控制台 得知请求路径
//返回的结果和前端已经"达成协议" 返回数据已经固定 R<> 具体返回什么泛型 要看前端需要什么参数
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        /*
        处理逻辑：
            ①. 将页面提交的密码password进行md5加密处理, 得到加密后的字符串
            ②. 根据页面提交的用户名username查询数据库中员工数据信息
            ③. 如果没有查询到, 则返回登录失败结果
            ④. 密码比对，如果不一致, 则返回登录失败结果
            ⑤. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
            ⑥. 登录成功，将员工id存入Session, 并返回登录成功结果
         */
        //①. 将页面提交的密码password进行md5加密处理, 得到加密后的字符串
        String password = employee.getPassword();
        // 进行md5 解码
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        //②. 根据页面提交的用户名username查询数据库中员工数据信息
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        // getOne() 根据用户名去查询用户 并且查询出来的只有一条信息 因为 在Employee表中 username做了唯一约束
        Employee emp = employeeService.getOne(queryWrapper);

        //③. 如果没有查询到, 则返回登录失败结果
        if (emp==null) {
            return R.error("登录失败");
        }

        //④. 密码比对，如果不一致, 则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //⑤. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()==0) {
            return R.error("账号已禁用");
        }

        //⑥. 登录成功，将员工id存入Session, 并返回登录成功结果
        HttpSession session = request.getSession();
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清除Session中保存的当前登录员工的id
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 新增员工
     * @param employee
     * @return
     */
    // @RequestBody 接收post请求的参数
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工信息,{}",employee);
        //还有一些数据没有传递(表单没有写)所以要手动 封装

        //设置初始密码123456,需要MD5加密  DigestUtils.md5DigestAsHex("123456".getBytes()) 加密固定写法
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //LocalDateTime.now() 系统当前时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        // 获取当前用户id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        /*
        第一种处理异常的方式
        try {
            employeeService.save(employee);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("新增员工失败");
        }
        */

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    //查看前端代码 确定返回值类型

    /**
     * 员工信息的分页查询
     * @param page 页数
     * @param pageSize 一页显示的条数
     * @param name 查询的字段
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //添加一个过滤条件  StringUtils.isNotEmpty(name) 判断 name是否等于null
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * 根据ID 修改员工信息 编辑和禁用是复用的
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        log.info(employee.toString());

        Long id=Thread.currentThread().getId();
        log.info("线程id为{}",id);
        // 这里接收long型的数据 由于js只能接收精度为16位的数据 但是 id是18位的 所以后3位是 四舍五入的处理 可以在前端中测试 alter(18位数据)
        //所以在更新的时候 匹配不到id
        //Long empId=(Long)request.getSession().getAttribute("employee");
        //修改时间
        //employee.setUpdateTime(LocalDateTime.now());
        //修改者
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return  R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息  修改员工先查询 后修改
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        /**
         * 1). 点击编辑按钮时，页面跳转到add.html，并在url中携带参数[员工id]
         * 2). 在add.html页面获取url中的参数[员工id]
         * 3). 发送ajax请求，请求服务端，同时提交员工id参数
         * 4). 服务端接收请求，根据员工id查询员工信息，将员工信息以json形式响应给页面
         * 5). 页面接收服务端响应的json数据，通过VUE的数据绑定进行员工信息回显
         * 6). 点击保存按钮，发送ajax请求，将页面中的员工信息以json方式提交给服务端
         * 7). 服务端接收员工信息，并进行处理，完成后给页面响应
         * 8). 页面接收到服务端响应信息后进行相应处理
         */
        Employee employee = employeeService.getById(id);
        log.info("根据id查询员工信息");
        if (employee!=null) {
            return R.success(employee);
        }
        return  R.error("没有查询到员工信息");
    }


}
