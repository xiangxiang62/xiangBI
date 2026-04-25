/**
 * XiangBI File: src/main/java/com/panther/smartBI/manager/AiManager.java
 * Responsibility: Project source module.
 */
package com.panther.smartBI.manager;

import com.panther.smartBI.ai.TongYiQianWenClient;
import com.panther.smartBI.common.ErrorCode;
import com.panther.smartBI.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AI 管理类 - 调用千问模型
 *
 * @author Gin 琴酒
 * @data 2023/7/30 16:48
 */
@Service
@Slf4j
public class AiManager {

    @Resource
    private TongYiQianWenClient tongYiQianWenClient;

    /**
     * 简单对话
     *
     * @param message 用户消息
     * @return AI响应内容
     */
    public String doChat(String message) {
        try {
            String content = tongYiQianWenClient.simpleChat(message);

            if (content == null || content.trim().isEmpty()) {
                log.error("千问API返回内容为空");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 返回内容为空");
            }

            log.info("千问API调用成功");
            return content;

        } catch (Exception e) {
            log.error("调用千问API失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务调用失败：" + e.getMessage());
        }
    }

    /**
     * 带系统提示词的对话
     *
     * @param systemPrompt 系统提示词
     * @param userMessage  用户消息
     * @return AI响应内容
     */
    public String doChatWithSystem(String systemPrompt, String userMessage) {
        try {
            String content = tongYiQianWenClient.chatWithSystem(systemPrompt, userMessage);

            if (content == null || content.trim().isEmpty()) {
                log.error("千问API返回内容为空");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 返回内容为空");
            }

            log.info("千问API调用成功");
            return content;

        } catch (Exception e) {
            log.error("调用千问API失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务调用失败：" + e.getMessage());
        }
    }

    /**
     * 多轮对话
     *
     * @param messages 消息列表
     * @return AI响应内容
     */
    public String doChatWithMessages(java.util.List<TongYiQianWenClient.Message> messages) {
        try {
            TongYiQianWenClient.CompatibleChatResponse response = tongYiQianWenClient.doChat(messages);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                log.error("千问多轮对话响应异常");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应异常");
            }

            String content = response.getChoices().get(0).getMessage().getContent();
            if (content == null || content.trim().isEmpty()) {
                log.error("千问多轮对话返回内容为空");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 返回内容为空");
            }

            log.info("千问多轮对话调用成功");
            return content;

        } catch (Exception e) {
            log.error("调用千问多轮对话失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务调用失败：" + e.getMessage());
        }
    }

    // ========================== 以下是兼容旧版本的方法 ==========================

    /**
     * @deprecated 已废弃，请使用 {@link #doChat(String)} 方法
     */
    @Deprecated
    public String doChat(long modelId, String message) {
        log.warn("正在使用已废弃的方法 doChat(modelId, message)，modelId参数已无效，建议使用 doChat(message)");
        return doChat(message);
    }

    /**
     * @deprecated 已废弃，请使用 {@link #doChat(String)} 方法
     */
    @Deprecated
    public String doChatByClient(long modelId, String message) {
        log.warn("正在使用已废弃的方法 doChatByClient(modelId, message)，modelId参数已无效，建议使用 doChat(message)");
        return doChat(message);
    }
}
