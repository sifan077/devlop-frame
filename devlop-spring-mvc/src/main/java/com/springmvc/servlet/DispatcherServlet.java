package com.springmvc.servlet;

import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;
import com.springmvc.context.WebApplicationContext;
import com.springmvc.handler.MyHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DispatcherServlet extends HttpServlet {

    // 指定SpringMVC容器
    private WebApplicationContext webApplicationContext;

    // 创建集合用于存储映射关系
    List<MyHandler> handlerList = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        // 1.加载初始化参数
        String contextConfigLocation = this.getServletConfig().getInitParameter("contextConfigLocation");
        // 2.创建Spring MVC容器
        webApplicationContext = new WebApplicationContext(contextConfigLocation);
        // 3.初始化操作
        webApplicationContext.onRefresh();
        // 4. 初始化请求映射关系
        initHandlerMapping();
    }

    /**
     * 初始化请求映射关系
     */
    private void initHandlerMapping() {
        Map<String, Object> iocMap = webApplicationContext.getIocMap();
        iocMap.forEach((k, v) -> {
            // 获取Class
            Class<?> clazz = v.getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    if (declaredMethod.isAnnotationPresent(RequestMapping.class)) {
                        String url = declaredMethod.getDeclaredAnnotation(RequestMapping.class).value();
                        // 建立 映射地址 和 控制器、方法的映射
                        MyHandler handler = new MyHandler(url, v, declaredMethod);
                        handlerList.add(handler);
                    }
                }
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 进行请求分发处理
        doDispatcher(req, resp);
    }

    /**
     * 进行请求分发处理
     *
     * @param req
     * @param resp
     */
    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) {
        // 根据用户的请求地址 查找Handler
        String uri = req.getRequestURI();
        MyHandler handler = findRequestHandler(uri);
        try {
            if (Objects.isNull(handler)) {
                resp.getWriter().write("<h1>404 Not Found</h1>");
            } else {
                // 调用方法前进行参数注入
                Object invoke = handler.getMethod().invoke(handler.getController());
//                if (Objects.isNull(invoke)) return;
//                else {
//                    req.getRequestDispatcher("success.jsp").forward(req, resp);
//                }

                if (invoke instanceof String) {
                    String viewName = (String) invoke;
                    if (viewName.contains(":")) {
                        String viewType = viewName.split(":")[0];
                        String viewPage = viewName.split(":")[1];
                        if (viewType.equals("forward")) {
                            req.getRequestDispatcher(viewPage).forward(req, resp);
                        } else {
                            // redirect
                            resp.sendRedirect(viewPage);
                        }
                    } else {
                        req.getRequestDispatcher(viewName + ".jsp").forward(req, resp);
                    }
                }
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException | ServletException e) {
            e.printStackTrace();
        }


    }

    /**
     * 发现请求处理程序
     *
     * @param uri uri
     * @return {@link MyHandler}
     */
    private MyHandler findRequestHandler(String uri) {
        for (MyHandler handler : handlerList) {
            if (handler.getUrl().equals(uri)) {
                return handler;
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
