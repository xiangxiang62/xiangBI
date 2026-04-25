/**
 * XiangBI File: src/main/java/com/panther/smartBI/service/impl/ChartServiceImpl.java
 * Responsibility: Service layer for business orchestration.
 */
package com.panther.smartBI.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panther.smartBI.common.ErrorCode;
import com.panther.smartBI.constant.CommonConstant;
import com.panther.smartBI.exception.BusinessException;
import com.panther.smartBI.exception.ThrowUtils;
import com.panther.smartBI.manager.AiManager;
import com.panther.smartBI.mapper.ChartMapper;
import com.panther.smartBI.model.dto.chart.ChartQueryRequest;
import com.panther.smartBI.model.dto.chart.GenChartByAiRequest;
import com.panther.smartBI.model.entity.Chart;
import com.panther.smartBI.model.entity.User;
import com.panther.smartBI.model.enums.ChartStatusEnum;
import com.panther.smartBI.model.vo.BiResponse;
import com.panther.smartBI.service.ChartService;
import com.panther.smartBI.service.UserService;
import com.panther.smartBI.utils.SqlUtils;
import com.panther.smartBI.utils.UserInputUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service("chartService")
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements ChartService {

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private ThreadPoolExecutor theadPoolExecutor;

    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String chartType = chartQueryRequest.getChartType();
        String goal = chartQueryRequest.getGoal();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
        String chartName = chartQueryRequest.getName();

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "userId", id);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.like(StringUtils.isNotBlank(chartName), "name", chartName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        return queryWrapper;
    }

    private void validGenChartRequest(String goal, String chartName) {
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(chartName) && chartName.length() > 100,
                ErrorCode.PARAMS_ERROR, "图表名称过长");
    }

    private String buildChartPrompt(String csvData, String goal, String chartType) {
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        userInput.append("\n请严格按照以下格式返回数据（分隔符=>=>=>必须单独占一行）：\n");
        userInput.append("=>=>=>\n");
        userInput.append("{ECharts图表配置JSON}\n");
        userInput.append("=>=>=>\n");
        userInput.append("{数据分析结论文本}\n");
        userInput.append("=>=>=>\n\n");
        userInput.append("要求：\n");
        userInput.append("1. 图表配置必须是合法的ECharts JSON格式\n");
        userInput.append("2. 分析结论要包含：数据概览、趋势分析、异常点、改进建议\n");
        userInput.append("3. 不要有任何额外说明文字，只返回上述格式的内容\n");
        return userInput.toString();
    }

    private String[] parseAiResult(String aiRes) {
        final String splitFlag = "=>=>=>";
        String[] aiData = aiRes.split(splitFlag);
        ThrowUtils.throwIf(aiData.length < 3, ErrorCode.SYSTEM_ERROR, "AI生成错误");

        String genChart = aiData[1].trim();
        String genResult = aiData[2].trim();
        ThrowUtils.throwIf(StringUtils.isBlank(genChart) || StringUtils.isBlank(genResult),
                ErrorCode.SYSTEM_ERROR, "AI 分析失败!");

        try {
            if (!genChart.startsWith("{")) {
                int start = genChart.indexOf("{");
                int end = genChart.lastIndexOf("}");
                if (start != -1 && end != -1 && end > start) {
                    genChart = genChart.substring(start, end + 1);
                }
            }
            cn.hutool.json.JSONUtil.parseObj(genChart);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成的图表格式不正确");
        }
        return new String[]{genChart, genResult};
    }

    private String[] generateChartResult(String csvData, String goal, String chartType) {
        String userInput = buildChartPrompt(csvData, goal, chartType);
        String aiRes = aiManager.doChat(userInput);
        return parseAiResult(aiRes);
    }

    private void markChartFailed(Chart update, String execMessage) {
        update.setStatus(ChartStatusEnum.CHART_STATUS_FAILURE.getValue());
        update.setExecMessage(StringUtils.isNotBlank(execMessage)
                ? execMessage
                : ChartStatusEnum.CHART_STATUS_FAILURE.getText());
        this.updateById(update);
    }

    @Override
    public BiResponse getChartByAi(String csvData, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String goal = genChartByAiRequest.getGoal();
        String chartName = genChartByAiRequest.getName();
        String chartType = genChartByAiRequest.getChartType();
        validGenChartRequest(goal, chartName);

        User loginUser = userService.getLoginUser(request);
        String[] chartResult = generateChartResult(csvData, goal, chartType);
        String genChart = chartResult[0];
        String genResult = chartResult[1];

        boolean b = userService.updateUserChartCount(request);
        ThrowUtils.throwIf(!b, ErrorCode.FORBIDDEN_ERROR, "次数用完请联系管理员");

        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setName(chartName);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        chart.setStatus(ChartStatusEnum.CHART_STATUS_SUCCESS.getValue());
        chart.setExecMessage(ChartStatusEnum.CHART_STATUS_SUCCESS.getText());
        ThrowUtils.throwIf(!this.save(chart), ErrorCode.SYSTEM_ERROR, "图表保存失败");

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        return biResponse;
    }

    @Override
    public BiResponse ByAiAsync(String csvData, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String goal = genChartByAiRequest.getGoal();
        String chartName = genChartByAiRequest.getName();
        String chartType = genChartByAiRequest.getChartType();
        validGenChartRequest(goal, chartName);

        User loginUser = userService.getLoginUser(request);

        Chart chart = new Chart();
        chart.setGoal(goal);
        chart.setName(chartName);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setStatus(ChartStatusEnum.CHART_STATUS_WAITING.getValue());
        chart.setExecMessage(ChartStatusEnum.CHART_STATUS_WAITING.getText());
        ThrowUtils.throwIf(!this.save(chart), ErrorCode.SYSTEM_ERROR, "图表保存失败");

        Long chartId = chart.getId();
        CompletableFuture.runAsync(() -> {
            Chart update = new Chart();
            update.setId(chartId);
            try {
                update.setStatus(ChartStatusEnum.CHART_STATUS_RUNNING.getValue());
                update.setExecMessage(ChartStatusEnum.CHART_STATUS_RUNNING.getText());
                ThrowUtils.throwIf(!this.updateById(update), ErrorCode.OPERATION_ERROR, "图表状态更新失败");

                String[] chartResult = generateChartResult(csvData, goal, chartType);
                String genChart = chartResult[0];
                String genResult = chartResult[1];

//                boolean b = userService.updateUserChartCount(request);
//                ThrowUtils.throwIf(!b, ErrorCode.FORBIDDEN_ERROR, "次数用完请联系管理员");

                update.setStatus(ChartStatusEnum.CHART_STATUS_SUCCESS.getValue());
                update.setExecMessage(ChartStatusEnum.CHART_STATUS_SUCCESS.getText());
                update.setGenChart(genChart);
                update.setGenResult(genResult);
                this.updateById(update);
            } catch (Exception e) {
                log.error("async gen chart failed, chartId={}", chartId, e);
                markChartFailed(update, e.getMessage());
            }
        }, theadPoolExecutor);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chartId);
        biResponse.setGenStatus(ChartStatusEnum.CHART_STATUS_WAITING.getValue());
        biResponse.setExecMessage(ChartStatusEnum.CHART_STATUS_WAITING.getText());
        return biResponse;
    }

    @Override
    public long saveRawData(String csvData, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        validGenChartRequest(goal, name);

        User loginUser = userService.getLoginUser(request);
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus(ChartStatusEnum.CHART_STATUS_WAITING.getValue());
        chart.setExecMessage(ChartStatusEnum.CHART_STATUS_WAITING.getText());
        chart.setUserId(loginUser.getId());
        ThrowUtils.throwIf(!this.save(chart), ErrorCode.SYSTEM_ERROR, "图表保存失败");
        return chart.getId();
    }

    @Override
    public List<Long> getFailedChart() {
        return baseMapper.getFailedChart();
    }

    @Override
    public boolean reloadChartByAi(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id < 0, ErrorCode.PARAMS_ERROR);
        userService.getLoginUser(request);
        CompletableFuture.runAsync(() -> {
            Chart oldChart = this.getById(id);
            ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);

            Chart update = new Chart();
            update.setId(id);
            try {
                update.setStatus(ChartStatusEnum.CHART_STATUS_RUNNING.getValue());
                update.setExecMessage(ChartStatusEnum.CHART_STATUS_RUNNING.getText());
                ThrowUtils.throwIf(!this.updateById(update), ErrorCode.OPERATION_ERROR, "图表状态更新失败");

                String[] chartResult = generateChartResult(oldChart.getChartData(), oldChart.getGoal(), oldChart.getChartType());
                String genChart = chartResult[0];
                String genResult = chartResult[1];

//                boolean b = userService.updateUserChartCount(request);
//                ThrowUtils.throwIf(!b, ErrorCode.FORBIDDEN_ERROR, "次数用完请联系管理员");

                update.setStatus(ChartStatusEnum.CHART_STATUS_SUCCESS.getValue());
                update.setExecMessage(ChartStatusEnum.CHART_STATUS_SUCCESS.getText());
                update.setGenChart(genChart);
                update.setGenResult(genResult);
                this.updateById(update);
            } catch (Exception e) {
                log.error("reload gen chart failed, chartId={}", id, e);
                markChartFailed(update, e.getMessage());
            }
        }, theadPoolExecutor);
        return true;
    }
}

