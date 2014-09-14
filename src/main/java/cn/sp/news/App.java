package cn.sp.news;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
 * 命令行界面
 *
 */
@SuppressWarnings("unchecked")
public class App {
	String urlStr = "http://rss.sina.com.cn/news/marquee/ddt.xml";
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<SyndEntry> news ;
	
	private int newsNo = 0;
	
	private SyndEntry currentNews;;
	
	private WebClient webClient = new WebClient();
	
	/**
	 * 链接rss
	 */
	public void rss() {
		try {
			//welcome
			output2Client("欢迎来到新闻客户端,正在读取新闻，请稍后。");
			URL feedUrl = new URL(urlStr);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
			output2Client("读取新闻完毕，按f健加回车，读下一条新闻。");
			news = feed.getEntries();

			SyndEntry e = news.get(newsNo);
			// 读条新闻
			output2Client(e);

			onLoop();
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (FeedException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void onLoop() {
		output2Client("请输入命令");
		Scanner in = new Scanner(System.in);
		String command = in.nextLine().toLowerCase();
		SoundMan.getSingleton().sayMsg("你输入的命令选项为 " + command);
//		switch (command) {
//		case "f":
//			showNextNews();
//			break;
//		case "r":
//			showPreviousNews();
//			break;
//		case "g":
//			showDetail();
//			break;
//		case "d":
//			backward();
//			break;
//		case "3":
//			System.exit(0);
//			break;
//		default:
//			break;
//		}
		onLoop();
	}

	private void backward() {
		// TODO Auto-generated method stub
		
	}

	
	private void showDetail() {
		String link = currentNews.getLink();
		if (link==null) {
			output2Client("不存在明细");
		}
		try {
			HtmlPage page = webClient.getPage(link);
			List<HtmlParagraph> ls = (List<HtmlParagraph>) page.getByXPath("//div[@id='artibody']/p");
			
			for (HtmlParagraph domElement : ls) {
				String p = domElement.getTextContent();
				output2Client(p);
			}
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void showPreviousNews() {
		if (newsNo<=0) {
			output2Client("已经是第一条新闻了,按f键加回车，读下一条新闻");
		}
		SyndEntry e = news.get(--newsNo);
		output2Client(e);
	}

	private void showNextNews() {
		if (newsNo>=news.size()-1) {
			output2Client("已经是最后一条新闻了,按r键加回车，读上一条新闻");
		}
		SyndEntry e = news.get(++newsNo);
		output2Client(e);
		
		
	}

	protected void output2Client(SyndEntry e) {
		currentNews = e;
		output2Client(e.getTitle().trim());
		String description = e.getDescription().getValue();
		output2Client(description.trim());
		output2Client("按g键加回车，前进到明细内容。按f键,读下一条新闻。");
	}

	private void output2Client(final String string) {
		SoundMan.getSingleton().acceptMsg(string);
	}

	/**
	* 通过代理链接rss
	*/
	public void agentRss() {
		try {
			URLConnection feedUrl = new URL(urlStr).openConnection();
			feedUrl.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (FeedException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *设置HTTP代理
	 */
	public void setHttpProxy() {
		Properties systemSettings = System.getProperties();
		systemSettings.put("http.proxyHost", "proxy01.cnooc");
		systemSettings.put("http.proxyPort", "8080");
		System.setProperties(systemSettings);
	}

	/**
	 *显示信息
	 */
	public void show(SyndFeed feed) {
		List list = feed.getEntries();
		for (int i = 0; i < list.size(); i++) {
			SyndEntry entry = (SyndEntry) list.get(i);
			logger.info(entry.getTitle() + " | " + entry.getAuthor() + " | " + entry.getLink());
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		App m = new App();
		m.setHttpProxy();
		m.rss();
	}
}
