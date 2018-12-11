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
    }
}
