package cn.sp.news;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author 陈嘉镇
* @version 创建时间：2014-9-12 下午2:54:16
* @email benjaminchen555@gmail.com
*/
public class HttpClient {
	CloseableHttpClient httpclient = HttpClients.createDefault();
	private Logger logger = LoggerFactory.getLogger(getClass());

	public List<String> getNews(String url) {
		try {
			getNewsContent(url);
		} catch (Exception e) {
			logger.error("error.", e);
		}
		return null;

	}

	protected String getNewsContent(String url) throws IOException, ClientProtocolException {
		HttpGet httpget = new HttpGet(url);

		String proxyHost = "proxy01.cnooc";
		int proxyPort = 8080;
		RequestConfig config = getProxyConfig(proxyHost, proxyPort);
		httpget.setConfig(config);

		System.out.println("Executing request " + httpget.getRequestLine());

		// Create a custom response handler
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			@Override
			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					String charset = "gb2312";
					return entity != null ? EntityUtils.toString(entity, charset) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			}

		};
		String responseBody = httpclient.execute(httpget, responseHandler);
		System.out.println("----------------------------------------");
		return responseBody;
	}

	protected RequestConfig getProxyConfig(String proxyHost, int proxyPort) {
		HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		return config;
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		HttpClient c = new HttpClient();
		String ipHtml = c.getNewsContent("http://tech.sina.com.cn/i/2014-09-12/12259608369.shtml");
		c.extractKeyWordText(ipHtml, "id=\"artibody\"");
	}

	// 循环访问所有节点，输出包含关键字的值节点
	public void extractKeyWordText(String inputHTML, String keyword) {
		try {
			//生成一个解析器对象，用网页的 url 作为参数
			Parser parser = new Parser();
			parser.setInputHTML(inputHTML);
			//设置网页的编码,这里只是请求了一个 gb2312 编码网页
			parser.setEncoding("gb2312");
			//迭代所有节点, null 表示不使用 NodeFilter
			NodeFilter f = new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getText().startsWith("<div class=\"blkContainerSblkCon BSHARE_POP\" id=\"artibody\">")) {
						return true;
					} else {
						return false;
					}
				}
			};
			NodeList list = parser.extractAllNodesThatMatch(f);
			//从初始的节点列表跌倒所有的节点
			processNodeList(list, keyword);
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	private void processNodeList(NodeList list, String keyword) {
		//迭代开始
		SimpleNodeIterator iterator = list.elements();
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			//得到该节点的子节点列表
			NodeList childList = node.getChildren();
			//孩子节点为空，说明是值节点
			if (null == childList) {
				//得到值节点的值
				String result = node.toPlainTextString();
				//若包含关键字，则简单打印出来文本
				if (result.indexOf(keyword) != -1)
					System.out.println(result);
			} //end if
				//孩子节点不为空，继续迭代该孩子节点
			else {
				processNodeList(childList, keyword);
			}//end else
		}//end wile
	}

}
