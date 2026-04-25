/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/user/UserUpdateMyRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
