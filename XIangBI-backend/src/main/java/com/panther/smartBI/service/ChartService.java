/**
 * XiangBI File: src/main/java/com/panther/smartBI/service/ChartService.java
 * Responsibility: Service layer for business orchestration.
 */
package com.panther.smartBI.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panther.smartBI.model.dto.chart.ChartQueryRequest;
import com.panther.smartBI.model.dto.chart.GenChartByAiRequest;
import com.panther.smartBI.model.entity.Chart;
import com.panther.smartBI.model.vo.BiResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图表信息表(Chart)表服务接口
 *
 * @author makejava
 * @since 2023-07-29 21:27:53
 */
public interface ChartService extends IService<Chart> {

/**
 * 查询并返回对应业务数据。
 */
    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

/**
 * 查询并返回对应业务数据。
 */
    BiResponse  getChartByAi(String csvData , GenChartByAiRequest genChartByAiRequest, HttpServletRequest request);

/**
 * 执行 AI 图表相关处理逻辑。
 */
    BiResponse  ByAiAsync(String csvData , GenChartByAiRequest genChartByAiRequest, HttpServletRequest request);

/**
 * 保存当前业务数据。
 */
    long saveRawData(String csvData , GenChartByAiRequest genChartByAiRequest, HttpServletRequest request);

/**
 * 查询并返回对应业务数据。
 */
    List<Long> getFailedChart();

/**
 * 更新或重试处理对应业务数据。
 */
    boolean  reloadChartByAi(long id ,HttpServletRequest request);
}


