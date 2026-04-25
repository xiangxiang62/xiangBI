/**
 * XiangBI File: src/main/java/com/panther/smartBI/controller/AnnouncementController.java
 * Responsibility: Controller layer for request handling.
 */
package com.panther.smartBI.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panther.smartBI.annotation.AuthCheck;
import com.panther.smartBI.common.BaseResponse;
import com.panther.smartBI.common.DeleteRequest;
import com.panther.smartBI.common.ErrorCode;
import com.panther.smartBI.common.ResultUtils;
import com.panther.smartBI.constant.UserConstant;
import com.panther.smartBI.exception.BusinessException;
import com.panther.smartBI.exception.ThrowUtils;
import com.panther.smartBI.model.dto.announcement.AnnouncementAddRequest;
import com.panther.smartBI.model.dto.announcement.AnnouncementQueryRequest;
import com.panther.smartBI.model.dto.announcement.AnnouncementUpdateRequest;
import com.panther.smartBI.model.entity.Announcement;
import com.panther.smartBI.model.entity.User;
import com.panther.smartBI.service.AnnouncementService;
import com.panther.smartBI.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private UserService userService;

    private void fillAnnouncementUserName(Announcement announcement) {
        if (announcement == null || announcement.getUserId() == null) {
            return;
        }
        User user = userService.getById(announcement.getUserId());
        if (user != null) {
            announcement.setUserName(user.getUserName());
        }
    }

    private void fillAnnouncementUserName(List<Announcement> announcementList) {
        if (announcementList == null || announcementList.isEmpty()) {
            return;
        }
        for (Announcement announcement : announcementList) {
            fillAnnouncementUserName(announcement);
        }
    }

/**
 * 新增对应业务数据。
 */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addAnnouncement(@RequestBody AnnouncementAddRequest announcementAddRequest,
                                              HttpServletRequest request) {
        if (announcementAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementAddRequest, announcement);
        announcementService.validAnnouncement(announcement, true);
        User loginUser = userService.getLoginUser(request);
        announcement.setUserId(loginUser.getId());
        if (Integer.valueOf(1).equals(announcement.getStatus()) && announcement.getPublishTime() == null) {
            announcement.setPublishTime(new Date());
        }
        boolean result = announcementService.save(announcement);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(announcement.getId());
    }

/**
 * 删除对应业务数据。
 */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAnnouncement(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(announcementService.removeById(deleteRequest.getId()));
    }

/**
 * 更新或重试处理对应业务数据。
 */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAnnouncement(@RequestBody AnnouncementUpdateRequest announcementUpdateRequest) {
        if (announcementUpdateRequest == null || announcementUpdateRequest.getId() == null
                || announcementUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement oldAnnouncement = announcementService.getById(announcementUpdateRequest.getId());
        ThrowUtils.throwIf(oldAnnouncement == null, ErrorCode.NOT_FOUND_ERROR);
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementUpdateRequest, announcement);
        if (Integer.valueOf(1).equals(announcement.getStatus()) && announcement.getPublishTime() == null) {
            announcement.setPublishTime(new Date());
        }
        announcementService.validAnnouncement(announcement, false);
        boolean result = announcementService.updateById(announcement);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

/**
 * 查询并返回对应业务数据。
 */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Announcement> getAnnouncementById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = announcementService.getById(id);
        ThrowUtils.throwIf(announcement == null, ErrorCode.NOT_FOUND_ERROR);
        fillAnnouncementUserName(announcement);
        return ResultUtils.success(announcement);
    }

/**
 * 查询并返回对应业务数据。
 */
    @GetMapping("/get/vo")
    public BaseResponse<Announcement> getAnnouncementVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Announcement announcement = announcementService.getById(id);
        ThrowUtils.throwIf(announcement == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUserPermitNull(request);
        if (!Integer.valueOf(1).equals(announcement.getStatus()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        fillAnnouncementUserName(announcement);
        return ResultUtils.success(announcement);
    }

/**
 * 查询并返回对应业务数据。
 */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Announcement>> listAnnouncementByPage(
            @RequestBody AnnouncementQueryRequest announcementQueryRequest) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size),
                announcementService.getQueryWrapper(announcementQueryRequest));
        fillAnnouncementUserName(announcementPage.getRecords());
        return ResultUtils.success(announcementPage);
    }

/**
 * 查询并返回对应业务数据。
 */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Announcement>> listAnnouncementVOByPage(
            @RequestBody AnnouncementQueryRequest announcementQueryRequest, HttpServletRequest request) {
        if (announcementQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = announcementQueryRequest.getCurrent();
        long size = announcementQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUserPermitNull(request);
        if (!userService.isAdmin(loginUser)) {
            announcementQueryRequest.setStatus(1);
        }
        Page<Announcement> announcementPage = announcementService.page(new Page<>(current, size),
                announcementService.getQueryWrapper(announcementQueryRequest));
        fillAnnouncementUserName(announcementPage.getRecords());
        return ResultUtils.success(announcementPage);
    }
}

