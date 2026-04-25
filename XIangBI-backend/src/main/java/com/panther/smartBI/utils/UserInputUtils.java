/**
 * XiangBI File: src/main/java/com/panther/smartBI/utils/UserInputUtils.java
 * Responsibility: Utility helper module.
 */
package com.panther.smartBI.utils;

import com.panther.smartBI.model.entity.Chart;
import com.panther.smartBI.service.ChartService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Gin 琪酒
 * @data 2023/8/6 16:10
 */
public class UserInputUtils {

    public static String BuilderUserInput(long chartId, ChartService chartService) {
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            return null;
        }
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
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
}

