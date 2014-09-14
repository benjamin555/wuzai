package cn.sp.news;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sp.news.MainView.KeyOperation;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
* @author 陈嘉镇
* @version 创建时间：2014-9-14 上午10:03:52
* @email benjaminchen555@gmail.com
*/
@SuppressWarnings("unchecked")
public class NewsAnt {
	String urlStr = "http://rss.sina.com.cn/news/marquee/ddt.xml";
	private Logger logger = LoggerFactory.getLogger(getClass());

	private List<SyndEntry> news;

	private int newsNo = -1;

	private SyndEntry currentNews;;

	private WebClient webClient = new WebClient();
	
	private SoundMan soundMan = SoundMan.getSingleton();
	
	private HtmlPage detailPage ;

	public NewsAnt() {
		output2Client("欢迎来到新浪新闻客户端,正在读取新闻，请稍后。");
		output2Client("使用方向键导航；z健暂停；j键继续。");
		URL feedUrl = null;
		SyndFeed feed = null;
		try {
			feedUrl = new URL(urlStr);

			SyndFeedInput input = new SyndFeedInput();
			feed = input.build(new XmlReader(feedUrl));
			news = feed.getEntries();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			logger.error("error", e);
		} catch (FeedException e) {
			logger.error("error", e);
		} catch (IOException e) {
			logger.error("error", e);
		}
		output2Client("读取新闻完毕，按{0}键，读下一条新闻。",KeyOperation.DOWN.getDescription());
	}

	protected void output2Client(SyndEntry e) {
		currentNews = e;
		output2Client(e.getTitle().trim());
		String description = e.getDescription().getValue();
		output2Client(description.trim());
		output2Client("按{0}键，前进到明细内容。按{1}键,读下一条新闻。",KeyOperation.RIGHT.getDescription(),KeyOperation.DOWN.getDescription());
	}

	private void output2Client( String string) {
		
		soundMan.acceptMsg(string);
	}
	
	/**
	 * 
	 * @param string 字符串模版
	 * @param p 占位符
	 */
	private void output2Client( String string,String... p ) {
		string = StringUtil.formatMsg(string, p);
		output2Client(string);
	}
	
	public void backward() {
		soundMan.shutup();
		
	}

	
	public void showDetail() {
		String link = currentNews.getLink();
		if (link==null) {
			output2Client("不存在明细");
			return;
		}
		try {
			output2Client("正在加载明细，请稍后");
			detailPage = webClient.getPage(link);
			logger.info("link:{}",link);
			output2Client("按空格键，切换下个段落");
//			output2Client("按P键，查看评论");
			List<HtmlParagraph> ls = (List<HtmlParagraph>) detailPage.getByXPath("//div[@id='artibody']/p");
			for (HtmlParagraph domElement : ls) {
				String p = domElement.getTextContent();
				output2Client(p);
			}
			
		} catch (FailingHttpStatusCodeException e) {
			logger.error("error", e);
		} catch (MalformedURLException e) {
			logger.error("error", e);
		} catch (IOException e) {
			logger.error("error", e);
		}
		
	}

	public void showPreviousNews() {
		if (newsNo<=0) {
			output2Client("已经是第一条新闻了,按{0}键，读下一条新闻",KeyOperation.DOWN.getDescription());
		}
		SyndEntry e = news.get(--newsNo);
		output2Client(e);
	}

	public void showNextNews() {
		if (newsNo>=news.size()-1) {
			output2Client("已经是最后一条新闻了,按{0}，读上一条新闻",KeyOperation.UP.getDescription());
		}
		SyndEntry e = news.get(++newsNo);
		output2Client(e);
		
		
	}

	/**
	 * 展示当前前三条评论
	 */
	
	public void showComment() {
		if (detailPage!=null) {
			List<HtmlElement> ds = (List<HtmlElement>) detailPage.getByXPath("//p[@id='J_Post_Box_Count']/a");
			if (ds!=null&&ds.size()>0) {
				String href = ds.get(0).getAttribute("href");
				try {
					HtmlPage page  = webClient.getPage(href);
					List<String> list = (List<String>) page.getByXPath("//div[@class='comment_content J_Comment_Txt clearfix'][position<4]/div[@class='t_txt']/text()");
					for (String string : list) {
						output2Client(string);
					}
				} catch (FailingHttpStatusCodeException e) {
					logger.error("error", e);
				} catch (MalformedURLException e) {
					logger.error("error", e);
				} catch (IOException e) {
					logger.error("error", e);
				}
			}
		}else {
			output2Client("不存在新闻明细页");
		}
		
	}

}
