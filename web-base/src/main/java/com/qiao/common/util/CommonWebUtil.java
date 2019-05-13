package com.qiao.common.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import yhao.infra.common.util.CommonDateUtil;
import yhao.infra.common.util.CommonStringUtil;

/**
 * @Description:
 * @Created by ql on 2019/5/13/013 23:05
 * @Version: v1.0
 */
public class CommonWebUtil {
    private static Logger log = LoggerFactory.getLogger(CommonWebUtil.class);

    public CommonWebUtil() {
    }

    public static int getInt(String name, int defaultValue) {
        String resultStr = getRequestParameter(name);
        if (resultStr != null) {
            try {
                return Integer.parseInt(resultStr);
            } catch (Exception var4) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
        String resultStr = getRequestParameter(name);
        if (resultStr != null) {
            try {
                return BigDecimal.valueOf(Double.parseDouble(resultStr));
            } catch (Exception var4) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static String getString(String name, String defaultValue) {
        String resultStr = getRequestParameter(name);
        return resultStr != null && !"".equals(resultStr) && !"null".equals(resultStr) && !"undefined".equals(resultStr) ? resultStr : defaultValue;
    }

    public static Cookie getCookie(String cookieName) {
        if (CommonStringUtil.isEmpty(cookieName)) {
            return null;
        } else {
            Cookie[] cookies = getRequest().getCookies();
            if (cookies != null) {
                int len = cookies.length;

                for(int i = 0; i < len; ++i) {
                    Cookie cookie = cookies[i];
                    if (cookieName.equals(cookie.getName())) {
                        return cookie;
                    }
                }
            }

            return null;
        }
    }

    public static String[] getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isNotEmpty(ip)) {
            ip = ip.replaceAll(" ", "");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip.split(",");
    }

    public static String getMACAddress(String ip) {
        String macAddress = "";

        try {
            Process p = Runtime.getRuntime().exec(ip);
            InputStreamReader ir = new InputStreamReader(p.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for(int i = 1; i < 100; ++i) {
                String str = input.readLine();
                if (str != null) {
                    if (str.indexOf("MAC Address") > 1) {
                        macAddress = str.substring(str.indexOf("MAC Address") + 14, str.length());
                        break;
                    }

                    if (str.indexOf("MAC Address") > 1) {
                        macAddress = str.substring(str.indexOf("MAC 地址") + 14, str.length());
                        break;
                    }
                }
            }
        } catch (IOException var7) {
            log.error(var7.getMessage());
        }

        return macAddress;
    }

    public static Boolean getBoolean(String name, boolean defaultValue) {
        String resultStr = getRequestParameter(name);
        if (resultStr != null && !"".equals(resultStr.trim()) && !"null".equals(resultStr.toLowerCase().trim()) && !"undefined".equals(resultStr.toLowerCase().trim())) {
            return !resultStr.trim().equals("1") && !resultStr.trim().equalsIgnoreCase("true") ? false : true;
        } else {
            return defaultValue;
        }
    }

    public static double getDouble(String name, double defaultValue) {
        String resultStr = getRequestParameter(name);
        if (resultStr != null) {
            try {
                return Double.parseDouble(resultStr);
            } catch (Exception var5) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public static Date getDate(String date, String format) {
        String dateStr = getString(date, (String)null);
        return dateStr != null && !"".equals(dateStr) ? CommonDateUtil.formatStringToDate(format, dateStr) : null;
    }

    private static String getRequestParameter(String name) {
        HttpServletRequest request = getRequest();
        return request.getParameter(name);
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static ServletContext getServletContext() {
        return ContextLoader.getCurrentWebApplicationContext().getServletContext();
    }

    public static Map<String, String> getParamterMap() {
        Map<String, String> map = new HashMap();
        Enumeration paramNames = getRequest().getParameterNames();

        while(paramNames.hasMoreElements()) {
            String paramName = (String)paramNames.nextElement();
            String[] paramValues = getRequest().getParameterValues(paramName);
            map.put(paramName, StringUtils.join(paramValues, ","));
        }

        return map;
    }

    public static String getHostContext() {
        StringBuffer url = getRequest().getRequestURL();
        String uri = getRequest().getRequestURI();
        return url.delete(url.length() - uri.length(), url.length()).append(getRequest().getContextPath()).append("/").toString();
    }
}
