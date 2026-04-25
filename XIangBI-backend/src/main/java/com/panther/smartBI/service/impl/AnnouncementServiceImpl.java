/**
 * XiangBI File: src/main/java/com/panther/smartBI/service/impl/AnnouncementServiceImpl.java
 * Responsibility: Service layer for business orchestration.
 */
package com.panther.smartBI.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panther.smartBI.common.ErrorCode;
import com.panther.smartBI.constant.CommonConstant;
import com.panther.smartBI.exception.BusinessException;
import com.panther.smartBI.mapper.AnnouncementMapper;
import com.panther.smartBI.model.dto.announcement.AnnouncementQueryRequest;
import com.panther.smartBI.model.entity.Announcement;
import com.panther.smartBI.service.AnnouncementService;
import com.panther.smartBI.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {

    @Override
    public QueryWrapper<Announcement> getQueryWrapper(AnnouncementQueryRequest announcementQueryRequest) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        if (announcementQueryRequest == null) {
            return queryWrapper;
        }
        Long id = announcementQueryRequest.getId();
        String title = announcementQueryRequest.getTitle();
        Integer status = announcementQueryRequest.getStatus();
        Long userId = announcementQueryRequest.getUserId();
        String sortField = announcementQueryRequest.getSortField();
        String sortOrder = announcementQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.orderByDesc("priority");
        queryWrapper.orderByDesc("publishTime");
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                CommonConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        return queryWrapper;
    }

    @Override
    public void validAnnouncement(Announcement announcement, boolean add) {
        if (announcement == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = announcement.getTitle();
        String content = announcement.getContent();
        Integer status = announcement.getStatus();
        Integer priority = announcement.getPriority();

        if (add && (StringUtils.isAnyBlank(title, content) || status == null)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "title, content and status are required");
        }
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "title is too long");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 5000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content is too long");
        }
        if (status != null && status != 0 && status != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "invalid status");
        }
        if (priority != null && priority < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "priority must be >= 0");
        }
    }
}

