package cn.sp.news;

/* The Java Version MSC Project
 * All rights reserved.
 *
 * Licensed under the Iflytek License, Version 2.0.1008.1034 (the "License");
 * you may not use this file except in compliance with the License(appId).
 * You may obtain an AppId of this application at
 *
 *      http://www.voiceclouds.cn
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Document we provide for details.
 */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图像界面
 * @author cyhu
 * 2012-06-14
 */
@SuppressWarnings("serial")
public class MainView extends JFrame implements ActionListener {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private JPanel mMainJpanel;
	private JPanel mContentPanel;
	private static JFrame mJframe;
	private NewsAnt newsAnt;
	SoundMan soundMan = SoundMan.getSingleton();

	/**
	 * 界面初始化.
	 * 
	 */
	public MainView() {
		
		newsAnt = new NewsAnt();
		
		initKeyListener();
		
		//设置界面大小，背景图片
		showView();
	
		
		
		
	}

	protected void initKeyListener() {
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				//如果为z，则暂停
				if (KeyEvent.VK_Z == e.getKeyCode()) {
					soundMan.pause();
					return;
				}
				//如果为j，则继续
				if (KeyEvent.VK_J == e.getKeyCode()) {
					soundMan.reSay();
					return;
				}
				//如果为	SPACE，则切换下个段落
				if (KeyEvent.VK_SPACE == e.getKeyCode()) {
					soundMan.sayNext();
					return;
				}
				//如果为esc，则退出程序
				if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
					soundMan.sayAndDo("即将退出程序", new Action() {
						@Override
						public void exec() {
							exit();
						}
					});
					return;
				}
				
				soundMan.sayMsg("你输入的命令为 " + KeyEvent.getKeyText(e.getKeyCode()));
				

				if (KeyEvent.VK_UP == e.getKeyCode() || KeyEvent.VK_UP == e.getKeyCode()) {
					newsAnt.showPreviousNews();
				}
				if (KeyEvent.VK_DOWN == e.getKeyCode() || KeyEvent.VK_KP_DOWN == e.getKeyCode()) {
					newsAnt.showNextNews();
				}
				if (KeyEvent.VK_LEFT == e.getKeyCode() || KeyEvent.VK_KP_LEFT == e.getKeyCode()) {
					newsAnt.backward();
				}
				if (KeyEvent.VK_RIGHT == e.getKeyCode() || KeyEvent.VK_KP_RIGHT == e.getKeyCode()) {
					newsAnt.showDetail();
				}
				
//				if (KeyEvent.VK_P == e.getKeyCode() ) {
//					newsAnt.showComment();
//				}

			}
		});
	}

	/**
	 * 设置界面大小，背景图片
	 */
	protected void showView() {
		String imagePath = "res/bg.png";
		ImageIcon background = new ImageIcon(getClass().getClassLoader().getResource(imagePath));
		JLabel label = new JLabel(background);
		label.setBounds(0, 0, background.getIconWidth(), background.getIconHeight());
		getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));

		int frameWidth = background.getIconWidth();
		int frameHeight = background.getIconHeight();

		setSize(frameWidth, frameHeight);
		setResizable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GridLayout gridlayout = new GridLayout(0, 3);
		gridlayout.setHgap(10);
		mMainJpanel = new JPanel(gridlayout);
		mMainJpanel.setOpaque(false);

		mContentPanel = new JPanel(new BorderLayout());
		mContentPanel.setOpaque(false);
		mContentPanel.add(mMainJpanel, BorderLayout.CENTER);

		setLocationRelativeTo(null);
		setContentPane(mContentPanel);
		setVisible(true);
	}

	/**
	 * Demo入口函数.
	 * @param args
	 */
	public static void main(String args[]) {
		mJframe = new MainView();
	}

	public static JFrame getFrame() {
		return mJframe;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		logger.info(e.toString());
	}

	public JPanel getMainJpanel() {
		return mMainJpanel;
	}

	public JPanel getContePanel() {
		return mContentPanel;
	}

	protected void exit() {
		logger.info("exit");
		newsAnt = null;
		soundMan = null;
		dispose();
		System.exit(0);
	}

	public static enum KeyOperation {
		UP(KeyEvent.VK_UP, KeyEvent.getKeyText(KeyEvent.VK_UP)), DOWN(KeyEvent.VK_DOWN, KeyEvent
				.getKeyText(KeyEvent.VK_DOWN)), LEFT(KeyEvent.VK_LEFT, KeyEvent.getKeyText(KeyEvent.VK_LEFT)), RIGHT(
				KeyEvent.VK_RIGHT, KeyEvent.getKeyText(KeyEvent.VK_RIGHT))

		;

		int keyCode;
		String description;

		private KeyOperation(int keyCode, String description) {
			this.keyCode = keyCode;
			this.description = description;
		}

		public int getKeyCode() {
			return keyCode;
		}

		public void setKeyCode(int keyCode) {
			this.keyCode = keyCode;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

}
