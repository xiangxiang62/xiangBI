/**
 * XiangBI File: src/main/java/com/panther/smartBI/service/impl/StoryServiceImpl.java
 * Responsibility: Service layer for business orchestration.
 */
package com.panther.smartBI.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panther.smartBI.model.entity.Story;
import com.panther.smartBI.service.StoryService;
import com.panther.smartBI.mapper.StoryMapper;
import org.springframework.stereotype.Service;

@Service
public class StoryServiceImpl extends ServiceImpl<StoryMapper, Story>
    implements StoryService{

}





