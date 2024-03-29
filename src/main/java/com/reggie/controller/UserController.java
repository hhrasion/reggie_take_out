package com.reggie.controller;

import com.reggie.common.R;
import com.reggie.domain.Mail;
import com.reggie.domain.User;
import com.reggie.service.UserService;
import com.reggie.utils.EmailUtil;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailUtil emailUtil;

    /**
     * 发送邮件验证码
     * @param user
     * @return
     * @throws MessagingException
     */
    @PostMapping("/sendMsg")
    public R<String> sendMail(@RequestBody User user, HttpSession session) throws MessagingException {
        String email = user.getEmail();
        if (Objects.isNull(email)){
            return R.error("邮件发送失败");
        }
        // 生成随机的4位验证码
        Integer code = ValidateCodeUtils.generateValidateCode(4);

        // 发送邮件
        Mail mail =new Mail();
        String text = "您的验证码为："+ code +"，请勿泄露于他人！";
        mail.setFrom("1425613935@qq.com");
        mail.setTo(email);
        mail.setSubject("验证码");
        mail.setText(text);
//        emailUtil.sendEmail(mail.getFrom(), mail.getTo(), mail.getSubject(), mail.getText());

        System.out.println(code);

        // 将生成的验证码保存到session
        session.setAttribute("user",code);

        return R.error("邮件发送成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map userData, HttpSession session){

        log.info("登录数据：{}",userData);
        String code1 = session.getAttribute("user").toString();

        String code2 = (String) userData.get("code");
        if (Objects.isNull(code1) || !Objects.equals(code1, code2)){
            return R.error("验证码错误");
        }

        String email = (String) userData.get("email");

        User user = userService.getUser(email);

        // 用户不存在，进行注册
        if (Objects.isNull(user)){
            user = new User();
            user.setEmail(email);
            userService.newUser(user);
        }

        session.setAttribute("user",user.getId());
        // 用户存在，进行登录
        return R.success(user);

    }
}
