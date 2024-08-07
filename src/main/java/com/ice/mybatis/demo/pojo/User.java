package com.ice.mybatis.demo.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: User
 * @Description:
 * @Author: ice
 * @Date: 2021/5/25 11:37
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = -2230628820743132767L;

    // 用户ID
    private Long userId;
    // 部门ID
    private Long deptId;
    // 登录账号
    private String loginName;
    // 用户昵称
    private String userName;
    // 用户类型（00系统用户 01注册用户）
    private String userType;
    // 用户邮箱
    private String email;
    // 手机号码
    private String phonenumber;
    // 用户性别（0男 1女 2未知）
    private char sex;
    // 头像路径
    private String avatar;
    // 密码
    private String password;
    // 盐加密
    private String salt;
    // 帐号状态（0正常 1停用）
    private char status;
    // 删除标志（0代表存在 2代表删除）
    private char delFlag;
    // 最后登录IP
    private String loginIp;
    // 最后登录时间
    private Date loginDate;
    // 密码最后更新时间
    private Date pwdUpdateDate;
    // 创建者
    private String createBy;
    // 创建时间
    private Date createTime;
    // 更新者
    private String updateBy;
    // 更新时间
    private Date updateTime;
    // 备注
    private String remark;
}
