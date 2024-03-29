package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.domain.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1.将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(lambdaQueryWrapper);

        //3.如果又有查询到则返回登陆失败结果
        if (emp == null) {
            return R.error("登录失败1");
        }

        //4.密码比对，如果不一致返回登陆失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败2");
        }

        //5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号共已禁用");
        }

        //6.登录成功，将员工id存入Session冰返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        emp.setPassword("");

        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("推出成功");
    }

    /**
     * 员工新增
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());

        //设置初始密码123456，md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now().toString());
//        employee.setUpdateTime(LocalDateTime.now().toString());

        Long empId = (Long) request.getSession().getAttribute("employee");

//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        boolean save = employeeService.save(employee);
        if (save) {
            return R.success("新增员工成功");
        }
        return R.error("新增员工失败");


    }

    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        // 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        lambdaQueryWrapper.like(!Objects.isNull(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 员工信息修改
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("employee：{}",employee.toString());
        long id = Thread.currentThread().getId();
        log.info("当前线程的id: {}",id);
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now().toString());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

}
