package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper ;
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //先删除套餐
        //遍历表中数据status为1的
        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == 1) {
                throw new RuntimeException("套餐正在售卖中，不能删除");
            }
        }
        setmealMapper.delete(ids);
        //再删除套餐菜品
        setmealDishMapper.deleteBySetmealIds(ids);

    }

    @Override
    @Transactional
    public SetmealDTO getById(Long id) {
        SetmealDTO setmealDTO = new SetmealDTO();
        Setmeal setmeal = setmealMapper.getById(id);
        if (setmeal != null) {
            BeanUtils.copyProperties(setmeal, setmealDTO);
            List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealDishesBySetmealId(id);
            setmealDTO.setSetmealDishes(setmealDishes);
        }
        return setmealDTO;
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 1. 先查数据库里的套餐，判断原来是不是起售中
        Setmeal oldSetmeal = setmealMapper.getById(setmealDTO.getId());
        if (oldSetmeal.getStatus() == 1) {
            throw new RuntimeException("套餐正在售卖中，不能修改");
        }

        // 2. 把DTO copy到Setmeal实体
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 3. 更新套餐主表
        setmealMapper.update(setmeal);

        // 4. 删除旧的套餐菜品
        setmealDishMapper.deleteBySetmealIds(Collections.singletonList(setmealDTO.getId()));

        // 5. 给新的菜品设置setmealId
        Long setmealId = setmealDTO.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        // 6. 插入新的菜品
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void status(Long id, Integer status) {
        // 修改套餐状态
        setmealMapper.status(id, status);
    }
}
