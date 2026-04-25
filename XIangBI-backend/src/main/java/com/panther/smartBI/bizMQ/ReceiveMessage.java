/**
 * XiangBI File: src/main/java/com/panther/smartBI/bizMQ/ReceiveMessage.java
 * Responsibility: Project source module.
 */
package com.panther.smartBI.bizMQ;

import com.panther.smartBI.common.ErrorCode;
import com.panther.smartBI.constant.BiConstant;
import com.panther.smartBI.constant.BiMQConstant;
import com.panther.smartBI.exception.BusinessException;
import com.panther.smartBI.manager.AiManager;
import com.panther.smartBI.model.entity.Chart;
import com.panther.smartBI.model.enums.ChartStatusEnum;
import com.panther.smartBI.service.ChartService;
import com.panther.smartBI.utils.UserInputUtils;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ReceiveMessage {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @SneakyThrows
    @RabbitListener(queues = {BiMQConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }

        long chartId = Long.parseLong(message);
        String userInput = UserInputUtils.BuilderUserInput(chartId, chartService);

        Chart update = new Chart();
        update.setId(chartId);
        update.setStatus(ChartStatusEnum.CHART_STATUS_RUNNING.getValue());
        update.setExecMessage(ChartStatusEnum.CHART_STATUS_RUNNING.getText());
        if (!chartService.updateById(update)) {
            update.setStatus(ChartStatusEnum.CHART_STATUS_FAILURE.getValue());
            update.setExecMessage(ChartStatusEnum.CHART_STATUS_FAILURE.getText());
            chartService.updateById(update);
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图表生成失败");
        }

        String aiRes = aiManager.doChat(BiConstant.BI_MODEL_ID_S, userInput);
        final String splitFlag = "=>=>=>";
        String[] aiData = aiRes.split(splitFlag);
        if (aiData.length < 3) {
            update.setStatus(ChartStatusEnum.CHART_STATUS_FAILURE.getValue());
            update.setExecMessage(ChartStatusEnum.CHART_STATUS_FAILURE.getText());
            chartService.updateById(update);
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 分析失败");
        }

        String genChart = aiData[1].trim();
        String genResult = aiData[2].trim();
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
            update.setStatus(ChartStatusEnum.CHART_STATUS_FAILURE.getValue());
            update.setExecMessage(ChartStatusEnum.CHART_STATUS_FAILURE.getText());
            chartService.updateById(update);
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成的图表格式不正确");
        }

        if (StringUtils.isBlank(genChart) || StringUtils.isBlank(genResult)) {
            update.setStatus(ChartStatusEnum.CHART_STATUS_FAILURE.getValue());
            update.setExecMessage(ChartStatusEnum.CHART_STATUS_FAILURE.getText());
            chartService.updateById(update);
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 分析失败");
        }

        update.setStatus(ChartStatusEnum.CHART_STATUS_SUCCESS.getValue());
        update.setExecMessage(ChartStatusEnum.CHART_STATUS_SUCCESS.getText());
        update.setGenChart(genChart);
        update.setGenResult(genResult);
        chartService.updateById(update);
        channel.basicAck(deliveryTag, false);
    }
}

