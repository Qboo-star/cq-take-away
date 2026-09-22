package com.sky.mapper;


import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealDishIdsByDishIds(List<Long> dishIds);

    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    void deleteBySetmealIds(@Param("ids") List<Long> ids);

    List<SetmealDish> getSetmealDishesBySetmealId(Long id);


    void deleteBySetmealId(Long setmealId);
}
