package com.whd.ratel.spring.framework.webmvc.servlet;

import com.whd.ratel.spring.framework.context.ApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:22
 * @apiNote Describe the function of this class in one sentence
 **/
public class DispatchServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        ApplicationContext applicationContext = new ApplicationContext(config.getInitParameter(LOCATION));
        //初始化策略
        initStrategies(applicationContext);
    }


    /**
     * Initialize the strategy objects that this servlet uses.
     * <p>May be overridden in subclasses in order to initialize further strategy objects.
     */
    private void initStrategies(ApplicationContext context) {
        //文件上传解析，如果请求类型是Multipart将通过multiResolver进行文件解析
        initMultipartResolver(context);
        //本地化解析
        initLocaleResolver(context);
        //主题解析
        initThemeResolver(context);
        //通过handlerMapping将请求映射到处理器
        initHandlerMappings(context);
        //通过handlerAdapter进行多类型的参数动态匹配
        initHandlerAdapters(context);
        //如果过程中遇到解析异常，将交给handlerExceptionResolver进行解析
        initHandlerExceptionResolvers(context);
        //直接解析请求到视图名
        initRequestToViewNameTranslator(context);
        //通过viewResolver解析逻辑视图到具体视图实现
        initViewResolvers(context);
        //flash映射管理器
        initFlashMapManager(context);
    }

    private void initMultipartResolver(ApplicationContext context) {

    }

    private void initLocaleResolver(ApplicationContext context) {

    }

    private void initThemeResolver(ApplicationContext context) {

    }

    private void initHandlerMappings(ApplicationContext context) {

    }

    private void initHandlerAdapters(ApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {

    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {

    }

    private void initViewResolvers(ApplicationContext context) {

    }

    private void initFlashMapManager(ApplicationContext context) {

    }


}
