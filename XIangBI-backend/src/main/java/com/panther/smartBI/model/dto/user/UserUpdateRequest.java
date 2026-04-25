/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/user/UserUpdateRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.user;

import java.io.Serializable;
import lombok.Data;

@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 电话
     */
    private String phoneNum;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 积分
     */
    private Integer leftCount;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
