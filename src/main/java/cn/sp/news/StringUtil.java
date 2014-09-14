package cn.sp.news;

import java.text.MessageFormat;

/**
 * 字符串操作的工具类
 * 
 * @author yong.gao
 * 
 */
public class StringUtil {

	
	public static String formatMsg(String pattern, String... objs) {
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(pattern);
		String content = formatter.format(objs);
		return content;
	}
	

}
