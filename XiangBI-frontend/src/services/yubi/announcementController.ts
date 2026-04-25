/**
 * XiangBI File: src/services/yubi/announcementController.ts
 * Responsibility: Frontend service definition.
 */
// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addAnnouncement POST /api/announcement/add */
export async function addAnnouncementUsingPOST(
  body: API.AnnouncementAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/announcement/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteAnnouncement POST /api/announcement/delete */
export async function deleteAnnouncementUsingPOST(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/announcement/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getAnnouncementById GET /api/announcement/get */
export async function getAnnouncementByIdUsingGET(
  params: API.getAnnouncementByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseAnnouncement_>('/api/announcement/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getAnnouncementVOById GET /api/announcement/get/vo */
export async function getAnnouncementVOByIdUsingGET(
  params: API.getAnnouncementVOByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseAnnouncement_>('/api/announcement/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listAnnouncementByPage POST /api/announcement/list/page */
export async function listAnnouncementByPageUsingPOST(
  body: API.AnnouncementQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageAnnouncement_>('/api/announcement/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listAnnouncementVOByPage POST /api/announcement/list/page/vo */
export async function listAnnouncementVOByPageUsingPOST(
  body: API.AnnouncementQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageAnnouncement_>('/api/announcement/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** updateAnnouncement POST /api/announcement/update */
export async function updateAnnouncementUsingPOST(
  body: API.AnnouncementUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/announcement/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

