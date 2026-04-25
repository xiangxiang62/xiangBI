/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/user/UserLoginRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户登录请求
 *
 */
@Data
public class UserLoginRequest implements Serializable {

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 3191241716373120793L;

/**
 * 用户账号。
 */
    private String userAccount;

/**
 * 密码相关字段。
 */
    private String userPassword;
}

