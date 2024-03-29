package com.reggie.common;

import org.springframework.context.annotation.EnableMBeanExport;

/**
 * 自定义业务异常类
 */
public class CustomerException extends RuntimeException{
    public CustomerException(String message){
        super(message);
    }

}
