package com.cjy.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.common.Result;
import com.cjy.domain.Homework;
import com.cjy.dto.PublishHomeworkDTO;

@Service
public interface IHomeworkService extends IService<Homework> {

    Result publishHomework(PublishHomeworkDTO dto);

}
