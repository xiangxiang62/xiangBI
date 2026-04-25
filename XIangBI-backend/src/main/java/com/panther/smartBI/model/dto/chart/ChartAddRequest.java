/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/chart/ChartAddRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.chart;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 */
@Data
public class ChartAddRequest implements Serializable {

    //分析目标
/**
 * 分析目标。
 */
    private String goal;
    /**
     * 图表名称
     */
    private String name;
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
    //生成的图表信息
/**
 * AI 生成的图表配置。
 */
    private String genChart;
    //生成的分析结论
/**
 * 业务字段。
 */
    private String getResult;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
