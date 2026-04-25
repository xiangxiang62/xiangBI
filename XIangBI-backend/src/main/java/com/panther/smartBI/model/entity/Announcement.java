/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/entity/Announcement.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends Model<Announcement> {

    @TableId(type = IdType.ASSIGN_ID)
/**
 * 主键 id。
 */
    private Long id;

/**
 * 标题。
 */
    private String title;

/**
 * 内容。
 */
    private String content;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 优先级，值越大越靠前
     */
    private Integer priority;

/**
 * 发布时间。
 */
    private Date publishTime;

/**
 * 关联用户 id。
 */
    private Long userId;

    @TableField(exist = false)
/**
 * 关联用户名。
 */
    private String userName;

/**
 * 创建时间。
 */
    private Date createTime;

/**
 * 更新时间。
 */
    private Date updateTime;

    @TableLogic
/**
 * 逻辑删除标记。
 */
    private Integer isDelete;
}

