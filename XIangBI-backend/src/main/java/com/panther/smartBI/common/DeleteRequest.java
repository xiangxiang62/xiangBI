/**
 * XiangBI File: src/main/java/com/panther/smartBI/common/DeleteRequest.java
 * Responsibility: Shared common infrastructure module.
 */
package com.panther.smartBI.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
