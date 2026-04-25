/**
 * XiangBI File: src/main/java/com/panther/smartBI/service/AnnouncementService.java
 * Responsibility: Service layer for business orchestration.
 */
package com.panther.smartBI.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panther.smartBI.model.dto.announcement.AnnouncementQueryRequest;
import com.panther.smartBI.model.entity.Announcement;

public interface AnnouncementService extends IService<Announcement> {

/**
 * 查询并返回对应业务数据。
 */
    QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest);

/**
 * 新增对应业务数据。
 */
    void validAnnouncement(Announcement announcement, boolean add);
}

