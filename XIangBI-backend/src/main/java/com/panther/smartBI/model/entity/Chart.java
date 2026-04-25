/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/entity/Chart.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图表信息表(Chart)表实体类
 *
 * @author makejava
 * @since 2023-07-29 21:28:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chart extends Model<Chart> {
    //id
    @TableId(type = IdType.ASSIGN_ID)
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
     * 名称
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
    /**
     * 图表状态 0: wait-等待, 2: running-生成中,1:succeed-成功生成,-1:failed-生成失败
     */
    private Integer Status;
    /**
     * 执行信息
     */
    private String execMessage;
    //生成的图表信息
/**
 * AI 生成的图表配置。
 */
    private String genChart;
    //生成的分析结论
/**
 * AI 生成的分析结论。
 */
    private String genResult;
    //创建图标用户 id
/**
 * 关联用户 id。
 */
    private Long userId;
    //创建时间
/**
 * 创建时间。
 */
    private Date createTime;
    //更新时间
/**
 * 更新时间。
 */
    private Date updateTime;
    //是否删除
    @TableLogic
/**
 * 逻辑删除标记。
 */
    private Integer isDelete;
}


