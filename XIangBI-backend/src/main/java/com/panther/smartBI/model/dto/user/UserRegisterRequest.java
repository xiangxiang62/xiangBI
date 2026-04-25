/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/user/UserRegisterRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册请求体
 *
 */
@Data
public class UserRegisterRequest implements Serializable {

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

/**
 * 密码相关字段。
 */
    private String checkPassword;
}

