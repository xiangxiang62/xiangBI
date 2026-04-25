/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/chart/GenChartByAiRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 */
@Data
public class GenChartByAiRequest implements Serializable {


    /**
     * 业务
     */
    private String goal;

    /**
     * 图表名称
     */
    private String name;

/**
 * 图表类型。
 */
    private String chartType;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
