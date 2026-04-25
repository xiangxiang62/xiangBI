/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/postfavour/PostFavourAddRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.postfavour;

import java.io.Serializable;
import lombok.Data;

/**
 * 帖子收藏 / 取消收藏请求
 *
 */
@Data
public class PostFavourAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
