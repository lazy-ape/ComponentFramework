package com.utlife.commonbeanandresource.bean;

/**
 * Created by xuqiang on 2017/4/5.
 */

/**
 * 关于进程的配置
 */
public class ProcessConfig {
    /**
     * 主进程的名称，对于在主进程中运行的library均使用该名称进行配置，
     * 在单独开发library时，修改该值为library进程的包名
     */
    public static final String MAIN_PROCESS_NAME = "com.utlife.user.greedywallet";

}
