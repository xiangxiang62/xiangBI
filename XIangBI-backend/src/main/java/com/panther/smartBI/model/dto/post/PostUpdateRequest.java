/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/post/PostUpdateRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.post;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class PostUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
