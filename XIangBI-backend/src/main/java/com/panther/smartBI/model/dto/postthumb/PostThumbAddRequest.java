/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/postthumb/PostThumbAddRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.postthumb;

import java.io.Serializable;
import lombok.Data;

/**
 * 帖子点赞请求
 *
 */
@Data
public class PostThumbAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
