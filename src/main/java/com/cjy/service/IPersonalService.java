package com.cjy.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cjy.domain.Personal;
import com.cjy.domain.Result;
import com.cjy.domain.dto.PersonalDTO;


@Service
public interface IPersonalService extends IService<Personal>{
    /**
     * 更新用户信息
     * @param id
     * @param personalDTO
     * @return
     */
    Result updatePersonalInfo(Long id, PersonalDTO personalDTO);
}
