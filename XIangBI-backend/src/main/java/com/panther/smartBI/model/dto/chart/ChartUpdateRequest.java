/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/chart/ChartUpdateRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.chart;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChartUpdateRequest implements Serializable {

/**
 * 主键 id。
 */
    private Long id;
    //分析目标
/**
 * 分析目标。
 */
    private String goal;
    /**
     * 图表名称
     */
    private String name;
    //图表类型
/**
 * 图表类型。
 */
    private String chartType;

    // 上传图表用户id
/**
 * 关联用户 id。
 */
    private Long userId;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
