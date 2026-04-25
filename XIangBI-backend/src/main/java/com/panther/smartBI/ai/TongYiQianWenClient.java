/**
 * XiangBI File: src/main/java/com/panther/smartBI/ai/TongYiQianWenClient.java
 * Responsibility: Project source module.
 */
package com.panther.smartBI.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用阿里云千问模型的客户端
 *
 * @author auto-generated
 * @date 2026-04-24
 */
@Slf4j
@Component
public class TongYiQianWenClient {

    private static final String COMPATIBLE_HOST = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    @Value("${aliyun.qianwen.api-key:}")
    private String apiKey;

    @Value("${aliyun.qianwen.model:qwen-turbo}")
    private String model;

    /**
     * 消息对象
     */
    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    /**
     * 兼容模式请求体
     */
    public static class CompatibleChatRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        private Integer maxTokens;
        private Double topP;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Integer getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }

        public Double getTopP() {
            return topP;
        }

        public void setTopP(Double topP) {
            this.topP = topP;
        }
    }

    /**
     * 兼容模式响应体
     */
    public static class CompatibleChatResponse {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public Long getCreated() {
            return created;
        }

        public void setCreated(Long created) {
            this.created = created;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }

        public Usage getUsage() {
            return usage;
        }

        public void setUsage(Usage usage) {
            this.usage = usage;
        }
    }

    public static class Choice {
        private Integer index;
        private Message message;
        private String finishReason;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }
    }

    public static class Usage {
        private Integer inputTokens;
        private Integer outputTokens;
        private Integer totalTokens;

        public Integer getInputTokens() {
            return inputTokens;
        }

        public void setInputTokens(Integer inputTokens) {
            this.inputTokens = inputTokens;
        }

        public Integer getOutputTokens() {
            return outputTokens;
        }

        public void setOutputTokens(Integer outputTokens) {
            this.outputTokens = outputTokens;
        }

        public Integer getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }
    }

    /**
     * 对话（兼容OpenAI格式）
     *
     * @param messages 消息列表
     * @return 响应结果
     */
    public CompatibleChatResponse doChat(List<Message> messages) {
        CompatibleChatRequest request = new CompatibleChatRequest();
        request.setModel(model);
        request.setMessages(messages);
        request.setTemperature(0.85);
        request.setMaxTokens(1500);
        request.setTopP(0.95);

        String json = JSONUtil.toJsonStr(request);
        log.debug("千问兼容模式请求：{}", json);

        String result = HttpRequest.post(COMPATIBLE_HOST)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(json)
                .execute()
                .body();

        log.debug("千问兼容模式响应：{}", result);

        return JSONUtil.toBean(result, CompatibleChatResponse.class);
    }

    /**
     * 简单单轮对话
     *
     * @param content 用户消息内容
     * @return AI回复内容
     */
    public String simpleChat(String content) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", content));

        CompatibleChatResponse response = doChat(messages);
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return null;
    }

    /**
     * 带系统提示词的对话
     *
     * @param systemPrompt 系统提示词
     * @param userMessage  用户消息
     * @return AI回复内容
     */
    public String chatWithSystem(String systemPrompt, String userMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.add(new Message("user", userMessage));

        CompatibleChatResponse response = doChat(messages);
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return null;
    }
}
