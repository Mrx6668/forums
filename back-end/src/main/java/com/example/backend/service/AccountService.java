package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.request.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account> , UserDetailsService {
    /*                                                          ⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆
    在Spring Security中，当用户尝试登录时，会触发一个身份验证过程1。这个过程通常涉及到以下步骤：
用户提交他们的用户名和密码。
Spring Security使用你提供的UserDetailsService（在你的代码中是AccountService）来加载用户详细信息，这些详细信息包括用户名、密码和授权信息。
Spring Security会取出用户提交的密码和UserDetailsService加载出来的密码进行比较。如果这两个密码匹配，那么身份验证就会成功。
在你的代码中，AccountService可能有一个方法用于加载用户详细信息（例如，通过用户名查找用户）。这个方法会返回一个UserDetails对象，其中包含了用户的用户名和密码。这个密码应该是从数据库中获取的用户密码。

然后，Spring Security会自动完成密码的比较工作1。如果用户提交的密码和数据库中存储的密码匹配，那么身份验证就会成功，用户就可以登录了。
     */
    Account findAccountByIdOrEmail(String text);
    String registerEmailVerifyCode(String type, String email, String ip);
    String registerEmailAccount(EmailRegisterVO vo);
    String resetConfirm(ConfirmResetVO resetVO);
    String resetEmailAccountPassword(EmailResetVO resetVO);
    Account findAccountById(int userId);
    String modifyEmail(int id, EmailModifyVO vo);
    boolean changePassword(int userId, PwdChangeVO vo);


}
