package com.chain.common.base;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author LiYongQiang
 * @Descrition
 * @Date 2021/9/8 4:00 下午
 */
@Data
public class PageBaseEntity extends PageSupport implements Serializable {

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 0 正常，1失效 ，99 删除
     */
    private Integer dr;
}
