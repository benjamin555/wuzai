package cn.sp.news;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
* @author 陈嘉镇
* @version 创建时间：2014-10-18 上午10:13:00
* @email benjaminchen555@gmail.com
*/
public class WebClientTest {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private WebClient webClient = new WebClient();
	
	@Test
	public void testGetPage() throws Exception {
		String link = "http://news.sina.com.cn/c/2014-10-18/075331008361.shtml";
		HtmlPage detailPage = webClient.getPage(link);
		logger.info(detailPage.asText());
	}
	
	
	@Test
	public void testParse() throws Exception {
		String link = "http://news.sina.com.cn/c/2014-10-18/075331008361.shtml";
		Parser parser = new Parser();
		parser.setURL(link);
		parser.setEncoding("UTF-8");
		NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("id","J_Comment_List_Hot"));
		NodeList list = parser.extractAllNodesThatMatch(filter);
		logger.info(list.size()+"");
	}

}
