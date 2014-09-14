package cn.sp.news;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.speech.SynthesizerPlayerListener;

/**
* @author 陈嘉镇
* @version 创建时间：2014-9-11 上午11:32:01
* @email benjaminchen555@gmail.com
*/
public class SoundMan extends Thread {

	private static final String APPID = "540ff189";

	private Logger logger = LoggerFactory.getLogger(getClass());

	private volatile boolean read = true;

	private SynthesizerPlayer synthesizer;

	private SoundMan() {
		if (SynthesizerPlayer.getSynthesizerPlayer() == null)
			SynthesizerPlayer.createSynthesizerPlayer("appid=" + APPID);
		synthesizer = SynthesizerPlayer.getSynthesizerPlayer();
		this.start();
	};

	private static SoundMan singleton = new SoundMan();

	private Queue<String> msgQueue = new LinkedBlockingQueue<String>();

	public static SoundMan getSingleton() {
		synchronized (singleton) {
			if (singleton == null) {
				singleton = new SoundMan();
			}
		}
		return singleton;
	}

	public void acceptMsg(final String msg) {
		logger.info("add {}", msg);
		msgQueue.offer(msg);
	}

	@Override
	public void run() {
		logger.info("Sound Man start.");
		//		loopThread = Thread.currentThread();
		while (true) {

			if (read && !msgQueue.isEmpty()) {
				String msg = msgQueue.poll();
				synthesize(msg);
			}
			//			try {
			//				Thread.sleep(1000);
			//			} catch (InterruptedException e) {
			//				e.printStackTrace();
			//			}
		}
	}

	/**
	 * 立即说出命令
	 * 终止之前的消息和清空消息队列;把当前消息加入消息队列中。
	 * @param msg
	 */
	public void sayMsg(String msg) {
		stopRead();
		msgQueue.clear();
		msgQueue.offer(msg);
		this.speak();

	}

	private void speak() {
		read = true;
	}

	/**
	 * 在完成当前阅读的前提下，停止阅读
	 */
	private void stopRead() {
		read = false;
	}

	/**
	 * 立即停止阅读
	 */
	public void shutup() {
		read = false;
		synthesizer.cancel();

	}

	private void synthesize(String text) {
		synthesizer.cancel();
		synthesizer.playText(text, null, synListener);
		stopRead();
	}

	/**
	 * SynthesizerPlayer Listener
	 */
	private SynthesizerPlayerListener synListener = new SynthesizerPlayerListener() {
		public void onBufferPercent(int percent, int beginPos, int endPos, String arg) {
			//			logger.info("*************缓冲进度" + percent + "**********");
		}

		public void onEnd(SpeechError error) {
			if (error == null)
				logger.info("*************会话成功*************");
			else{
				logger.info(error.toString());
			}
				

			speak();
		}

		public void onPlayBegin() {

		}

		public void onPlayPaused() {

		}

		public void onPlayPercent(int percent, int beginPos, int endPos) {

		}

		public void onPlayResumed() {

		}

	};

	/**
	 * 立即说出命令,并在结束后执行操作命令
	 * @param string
	 * @param action
	 */
	public void sayAndDo(String string, final Action action) {
		logger.info("sayAndDo");
		stopRead();
		synthesizer.cancel();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("error", e);
		}
		synthesizer.playText(string, null, new SynthesizerPlayerListener() {
			@Override
			public void onBufferPercent(int arg0, int arg1, int arg2, String arg3) {

			}

			@Override
			public void onEnd(SpeechError arg0) {
				logger.info("arg0:{}", arg0);
				action.exec();
			}

			@Override
			public void onPlayBegin() {

			}

			@Override
			public void onPlayPaused() {

			}

			@Override
			public void onPlayPercent(int arg0, int arg1, int arg2) {

			}

			@Override
			public void onPlayResumed() {

			}

		});
		speak();
	}

	/**
	 * 暂停
	 */
	public void pause() {
		stopRead();
		synthesizer.pause();
	}

	/**
	 * 继续
	 */
	public void reSay() {
		synthesizer.resume();

	}
	/**
	 * 切换下个段落
	 */
	public void sayNext() {
		synthesizer.cancel();
		speak();
		
	}

}
