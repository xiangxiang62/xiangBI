/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/dto/chart/ChartQueryRequest.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.dto.chart;

import com.panther.smartBI.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

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
    /**
     * 创建图标用户id
     */
    private Long userId;

/**
 * 序列化版本号。
 */
    private static final long serialVersionUID = 1L;
}
