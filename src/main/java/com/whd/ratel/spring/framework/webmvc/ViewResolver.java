package com.whd.ratel.spring.framework.webmvc;

import com.whd.ratel.spring.framework.webmvc.map.ModelAndView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/14 0:17
 * @apiNote
 * 1.将一个静态文件变成一个动态文件
 * 2.根据用户的传参不同产生不同的结果
 * 最终输出字符串，交给Response输出
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewResolver {

    private String viewName;

    private File template;

    private static Pattern pattern = Pattern.compile("￥\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);

    public String viewResolver(ModelAndView modelAndView) throws Exception {
        StringBuffer buffer = new StringBuffer();

        RandomAccessFile randomAccessFile = new RandomAccessFile(this.template, "r");

        String line;
        while (null != (line = randomAccessFile.readLine())){
            Matcher matcher = matcher(line);
            while (matcher.find()) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    //要把人名币符号￥{}中间的字符串取出来
                    String paramName = matcher.group(i);
                    Object paramValue = modelAndView.getModel().get(paramName);
                    if (null == paramValue){continue;}
                    line = line.replaceAll("￥\\{"+ paramName +"\\}", paramValue.toString());
                }
            }
            buffer.append(line);
        }
        return null;
    }

    /***
     * 正则匹配
     * @param str
     * @return
     */
    private Matcher matcher(String str){
        return pattern.matcher(str);
    }
}
