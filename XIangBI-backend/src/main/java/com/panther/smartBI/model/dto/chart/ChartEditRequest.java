/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/chart/ChartEditRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.chart;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求
 *
 */
@Data
public class ChartEditRequest implements Serializable {

    /**
     * 图标ID
     */
    private Long id;
    /**
     * 图表名称
     */
    private String name;

    //分析目标
/**
 * 分析目标。
 */
    private String goal;
    //图表信息
/**
 * 原始图表数据。
 */
    private String chartData;
    //图表类型
/**
 * 图表类型。
 */
    private String chartType;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
