package com.qiao.service.base;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import yhao.infra.service.datasource.BaseDao;
/**
 * @Description:
 * @Created by ql on 2019/4/24/024 22:01
 * @Version: v1.0
 */
public abstract class BaseService {
    private Logger logger = LoggerFactory.getLogger(BaseService.class);
    @Autowired
    private BaseDao mysqlBaseDao;

    public BaseService() {
    }

    public List commonQuery(String sqlMap, Object param) {
        return this.mysqlBaseDao.selectList(sqlMap, param);
    }
}
