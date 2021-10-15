package com.chain.common.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LiYongQiang
 * @Descrition 分页基类
 * @Date 2021/9/8 4:04 下午
 */
@Data
public class PageSupport implements Serializable {

    /**
     * 当前页码
     */
    private Integer pageNum;



    /**
     * 每页数量
     */
    private Integer pageSize;


    /**
     * 排序
     */
    private String orderBy;
}
