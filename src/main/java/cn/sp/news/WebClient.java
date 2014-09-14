package cn.sp.news;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;

/**
* @author 陈嘉镇
* @version 创建时间：2014-3-27 下午4:14:58
* @email benjaminchen555@gmail.com
*/
public class WebClient {
	private com.gargoylesoftware.htmlunit.WebClient webClient = new com.gargoylesoftware.htmlunit.WebClient();
	private Logger logger = LoggerFactory.getLogger(WebClient.class);

	private ThreadLocal<HtmlPage> threadPage = new ThreadLocal<HtmlPage>();

	/**
	 * 是否开启代理
	 */
	private boolean enableProxy =false;

	/**
	 * 代理服务器
	 */
	private String proxyServer = "proxy01.cnooc";

	/**
	 * 代理端口
	 */
	private int ProxyPort = 8080;

	public WebClient() {
		super();
		initClient();
	}

	/**
	 * 初始化
	 */
	public void initClient() {
		logger.info("enableProxy:{}", enableProxy);
		if (enableProxy) {
			logger.info("ProxyServer():{}", getProxyServer());
			ProxyConfig proxyConfig = new ProxyConfig(getProxyServer(), getProxyPort());
			webClient.getOptions().setProxyConfig(proxyConfig);
		}

		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setAppletEnabled(false);

		webClient.setJavaScriptTimeout(10000);
	}

	public void closeAllWindows() {
		webClient.closeAllWindows();
	}

	public HtmlPage getPage(String url2) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		return webClient.getPage(url2);
	}

	public HtmlPage getCurrentPage() {
		HtmlPage htmlPage = threadPage.get();
		logger.info("getCurrentPage:{}", htmlPage);
		return htmlPage;
	}

	public void setCurrentPage(HtmlPage currentPage) {
		logger.info("setCurrentPage:{}", currentPage);
		this.threadPage.set(currentPage);
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}

	public String getProxyServer() {
		return proxyServer;
	}

	public void setProxyServer(String proxyServer) {
		this.proxyServer = proxyServer;
	}

	public int getProxyPort() {
		return ProxyPort;
	}

	public void setProxyPort(int proxyPort) {
		ProxyPort = proxyPort;
	}

	public ThreadLocal<HtmlPage> getThreadPage() {
		return threadPage;
	}

	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient client = new WebClient();
		
		HtmlPage htmlPage = client.getPage("http://tech.sina.com.cn/i/2014-09-12/12259608369.shtml");
		
		List<HtmlParagraph> ls = (List<HtmlParagraph>) htmlPage.getByXPath("//div[@id='artibody']/p");
		
		for (HtmlParagraph htmlParagraph : ls) {
			System.out.println(htmlParagraph.getTextContent());
		}
	}
}
