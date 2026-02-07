package com.cjy.service;

import com.cjy.dto.HomeworkMessageDTO;

public interface IHomeworkMessageConsumer {
    void handlePublishMessage(HomeworkMessageDTO dto);
}
