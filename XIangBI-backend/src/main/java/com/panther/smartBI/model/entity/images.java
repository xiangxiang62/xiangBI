/**
 * XiangBI File: src/main/java/com/panther/smartBI/model/entity/images.java
 * Responsibility: Domain model and transfer object definition.
 */
package com.panther.smartBI.model.entity;

import lombok.Data;

/**
 * @author Gin 琴酒
 * @data 2023/8/7 15:46
 */
@Data
public class images {

/**
 * 主键 id。
 */
    private Integer id;

/**
 * 名称。
 */
    private String name;

/**
 * 业务字段。
 */
    private String mapName;

/**
 * 业务字段。
 */
    private String icon;

}

