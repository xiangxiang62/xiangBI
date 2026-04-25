/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/file/UploadFileRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传请求
 *
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
