/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/announcement/AnnouncementAddRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class AnnouncementAddRequest implements Serializable {

    /**
     * 标题。
     */
    private String title;

    /**
     * 内容。
     */
    private String content;

    /**
     * 状态值。
     */
    private Integer status;

    /**
     * 优先级。
     */
    private Integer priority;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
/**
 * 发布时间。
 */
    private Date publishTime;

    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;
}

