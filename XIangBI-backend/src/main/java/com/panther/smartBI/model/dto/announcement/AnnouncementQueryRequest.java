/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/announcement/AnnouncementQueryRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.announcement;

import com.panther.smartBI.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementQueryRequest extends PageRequest implements Serializable {

/**
 * 主键 id。
 */
    private Long id;

/**
 * 标题。
 */
    private String title;

/**
 * 状态值。
 */
    private Integer status;

/**
 * 关联用户 id。
 */
    private Long userId;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}

