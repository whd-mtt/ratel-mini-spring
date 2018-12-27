package com.whd.ratel.spring.framework.webmvc.servlet;

import com.whd.ratel.spring.framework.annotation.Controller;
import com.whd.ratel.spring.framework.annotation.RequestMapping;
import com.whd.ratel.spring.framework.annotation.RequestParam;
import com.whd.ratel.spring.framework.context.ApplicationContext;
import com.whd.ratel.spring.framework.webmvc.HandlerAdapter;
import com.whd.ratel.spring.framework.webmvc.HandlerMapping;
import com.whd.ratel.spring.framework.webmvc.ViewResolver;
import com.whd.ratel.spring.framework.webmvc.map.ModelAndView;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:22
 * @apiNote Describe the function of this class in one sentence
 **/
@Slf4j
public class DispatchServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    /***
     * handlerMapping是最核心的设计也是最经典的，它的存在直接干掉了struts/webWork等mvc框架
     */
    private List<HandlerMapping> handlerMappings = new ArrayList<>();

//    private List<HandlerAdapter> handlerAdapters = new ArrayList<>();
    /***
     * 这里为了实现方便，直接用map实现
     */
    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    /***
     *
     */
    private List<ViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatcher(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>"
                    + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", "\r\n")
                    + "<font color='green'><i>Copyright@whdMVC</i></font>");
            e.printStackTrace();
        }
    }

    /***
     *
     * @param req
     * @param resp
     */
    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //根据用户请求的url来取得handler
        HandlerMapping handlerMapping = getHandler(req);
        if (handlerMapping == null) {
            resp.getWriter().write("<font size='25' color='red'>404 Not Found</font><br/><font color='green'><i>Copyright@whdMVC</i></font>");
            return;
        }
        HandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
        //只是调用用方法，得到返回值
        ModelAndView modelAndView = handlerAdapter.handler(req, resp, handlerMapping);
        //这一步才是真正的输出
        processDispatchResult(resp, modelAndView);
    }

    /***
     * 将逻辑视图输出
     * @param resp
     * @param modelAndView
     */
    private void processDispatchResult(HttpServletResponse resp, ModelAndView modelAndView) throws Exception {

        //调用viewResolver的resolveView()方法
        if (null == modelAndView) { return; }
        if (this.viewResolvers.isEmpty()) { return; }
        for (ViewResolver viewResolver : viewResolvers) {
            if (!modelAndView.getViewName().equals(viewResolver.getViewName())) { continue; }
            String out = viewResolver.viewResolver(modelAndView);
            if (out != null) {
                resp.getWriter().write(out);
                break;
            }
        }
    }

    /***
     * 获取HandlerAdapter处理器适配器
     * @param handlerMapping
     * @return
     */
    private HandlerAdapter getHandlerAdapter(HandlerMapping handlerMapping) {
        if (this.handlerAdapters.isEmpty()) { return null; }
        return this.handlerAdapters.get(handlerMapping);
    }

    /***
     * 获取handlerMapping处理器映射器
     * @param req
     * @return
     */
    private HandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) { return null; }
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (HandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {continue;}
            return handlerMapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) {

        ApplicationContext applicationContext = new ApplicationContext(config.getInitParameter(LOCATION));
        //初始化策略
        initStrategies(applicationContext);
    }


    /**
     * Initialize the strategy objects that this servlet uses.
     * <p>May be overridden in subclasses in order to initialize further strategy objects.
     */
    private void initStrategies(ApplicationContext context) {

        // =============  这里说的就是springmvc的九大组件 ================
        //文件上传解析，如果请求类型是Multipart将通过multiResolver进行文件解析
        initMultipartResolver(context);
        //本地化解析
        initLocaleResolver(context);
        //主题解析
        initThemeResolver(context);
        //通过handlerMapping将请求映射到处理器
        //将controller中配置的RequestMapping和method映射一个对应关系
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

    /***
     * 将controller中配置的RequestMapping和method映射一个对应关系
     * @param context
     */
    private void initHandlerMappings(ApplicationContext context) {

        //按照通常的理解应该是一个map
        //Map<String, Method> map;
        //map.put(url, method)
        //先从从容器中取到所有的实例
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object instance = context.getBean(beanDefinitionName);
            Class<?> clazz = instance.getClass();
            //但是不是所有的bean都有Controller注解
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //扫描所有的public方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*"))
                        .replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(instance, method, pattern));
                log.info("mapping映射为Mappings: {}, {}", regex, method);
                System.out.println("Mappings: " + regex + ", " + method);
            }
        }
    }

    /***
     * 通过handlerAdapter进行多类型的参数动态匹配
     * @param context
     */
    private void initHandlerAdapters(ApplicationContext context) {
        //在初始化阶段，我哦们要做的是，将这些参数的名字或者类型按照一定得顺序保存下来，
        //因为后面用反射调用的时候，传入的形参是一个函数
        //可以通过记录这些参数的位置index挨个从数字中填值，这样的话就和参数的舒徐无关
        this.handlerMappings.forEach(handlerMapping -> {
            Map<String, Integer> params = new HashMap<>();
            //这里只是处理命名参数
            Annotation[][] parameterAnnotations = handlerMapping.getMethod().getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof RequestParam) {
                        String paramName = ((RequestParam) annotation).value();
                        if (!"".equals(paramName.trim())) {
                            params.put(paramName, i);
                        }
                    }
                }
            }
            //接下来我们处理非命名参数
            //只处理request和response
            //获取参数的参数类型
            Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    params.put(type.getName(), i);
                }
            }

            this.handlerAdapters.put(handlerMapping, new HandlerAdapter(params));
        });

    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {

    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {

    }

    /***
     * 通过viewResolver解析逻辑视图到具体视图实现
     * @param context
     */
    private void initViewResolvers(ApplicationContext context) {
        //在页面输入一个http://localhost:8080/first.html
        //解决一个页面名字和模板文件关联的问题
        String templates = context.getConfig().getProperty("templates");
        String templatesPath = this.getClass().getClassLoader().getResource(templates).getFile();
        File templatesDir = new File(templatesPath);
        //扫描该路径下的所有的模板文件
        File[] files = templatesDir.listFiles();
        if (Objects.nonNull(files) && files.length > 0) {
            for (File template : files) {
                this.viewResolvers.add(new ViewResolver(template.getName(), template));
            }
        }
    }

    private void initFlashMapManager(ApplicationContext context) {

    }


}
