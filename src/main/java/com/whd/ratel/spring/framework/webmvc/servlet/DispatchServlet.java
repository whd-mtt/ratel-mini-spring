package com.whd.ratel.spring.framework.webmvc.servlet;

import com.whd.ratel.spring.framework.context.ApplicationContext;
import com.whd.ratel.spring.framework.webmvc.HandlerAdapter;
import com.whd.ratel.spring.framework.webmvc.HandlerMapping;
import com.whd.ratel.spring.framework.webmvc.map.ModelAndView;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:22
 * @apiNote Describe the function of this class in one sentence
 **/
public class DispatchServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private List<HandlerAdapter> handlerAdapters = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = uri.replaceAll(contextPath, "").replaceAll("/+", "/");

//        try {
////            ModelAndView modelAndView = (ModelAndView) handlerMapping.getMethod().invoke(handlerMapping.getController(), null);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }

        //对象.方法名
//        method.invoke()

        doDispatcher(req, resp);
    }

    /***
     *
     * @param req
     * @param resp
     */
    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) {
       HandlerMapping handlerMapping =  getHandler(req);

       HandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);

       ModelAndView  modelAndView = handlerAdapter.handler(req, resp, handlerMapping);

       processDispatchResult(req, modelAndView);
    }

    private void processDispatchResult(HttpServletRequest req, ModelAndView modelAndView) {

        //调用viewResolver的resolveView()方法
    }

    /***
     * 获取HandlerAdapter处理器适配器
     * @param handlerMapping
     * @return
     */
    private HandlerAdapter getHandlerAdapter(HandlerMapping handlerMapping) {
        return null;
    }

    /***
     *  获取handlerMapping处理器映射器
     * @param req
     * @return
     */
    private HandlerMapping getHandler(HttpServletRequest req) {
        return null;
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
        /**
         * 将controller中配置的RequestMapping和method映射一个对应关系
         */
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

        //按照通常的理解应该是一个map
        //Map<String, Method> map;


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
