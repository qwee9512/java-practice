package main;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.*;

import main.BlockGame2.MyFrame.Block;

public class BlockGame
{

	static class MyFrame extends JFrame
	{
		// constant
		static int BALL_WIDTH = 20;
		static int BALL_HEIGHT = 20;
		static int BLOCK_ROWS = 5;
		static int BLOCK_COLUMNS = 10;
		static int BLOCK_WIDTH = 40;
		static int BLOCK_HEIGHT = 20;
		static int BLOCK_GAP = 3;
		static int BAR_WIDTH = 80;
		static int BAR_HEIGHT = 20;
		static int CANVAS_WIDTH = 400 + (BLOCK_GAP * BLOCK_COLUMNS) - BLOCK_GAP;
		static int CANVAS_HEIGHT = 600;

		// variable
		static MyPanel myPanel = null;
		static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLUMNS];
		static Bar bar = new Bar();
		static Ball ball = new Ball();
		static Timer timer = null;
		static int score = 0;
		static int barXTarget = bar.x; // Target Value - interpolation
		static int dir = 0; // 0 : Up-Right, 1 : Down-Right, 2 : Up-Left, 3 : Down-Left
		static int ballSpeed = 4;
		static boolean isGameFinish = false;

		static class Block
		{
			int x = 0;
			int y = 0;
			static int width = BLOCK_WIDTH;
			static int height = BLOCK_HEIGHT;
			int color = 0; // 0: white, 1: yello, 2: blue, 3: mazanta, 4: red
			boolean isHidden = false;
		}

		static class Bar
		{
			int x = (CANVAS_WIDTH - BAR_WIDTH) / 2;
			int y = CANVAS_HEIGHT - 100;
			int width = BAR_WIDTH;
			int height = BAR_HEIGHT;
		}

		static class Ball
		{
			int x = (CANVAS_WIDTH - BALL_WIDTH) / 2;
			int y = (CANVAS_HEIGHT - 100 - BALL_HEIGHT);

			Point getCenter()
			{
				return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT / 2));
			}

			Point getBottomCenter()
			{
				return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT));
			}

			Point getTopCenter()
			{
				return new Point(x + (BALL_WIDTH / 2), y);
			}

			Point getLeftCenter()
			{
				return new Point(x, y + (BALL_HEIGHT / 2));
			}

			Point getRightCenter()
			{
				return new Point(x + (BALL_WIDTH), y + (BALL_HEIGHT / 2));
			}
		}

		static class MyPanel extends JPanel
		{
			public MyPanel()
			{
				this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
				this.setBackground(Color.BLACK);
			}

			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g;

				drawUI(g2d);
			}

			public void drawUI(Graphics2D g2d)
			{
				// Draw Blocks
				for (int i = 0; i < BLOCK_ROWS; i++)
				{
					for (int j = 0; j < BLOCK_COLUMNS; j++)
					{
						if (blocks[i][j].isHidden)
							continue;
						switch (blocks[i][j].color)
						{ // 0: white, 1: yello, 2: blue, 3: mazanta, 4: red
						case 0:
						{
							g2d.setColor(Color.white);
							break;
						}
						case 1:
						{
							g2d.setColor(Color.yellow);
							break;
						}
						case 2:
						{
							g2d.setColor(Color.blue);
							break;
						}
						case 3:
						{
							g2d.setColor(Color.magenta);
							break;
						}
						case 4:
						{
							g2d.setColor(Color.red);
							break;
						}
						}
						g2d.fillRect(blocks[i][j].x, blocks[i][j].y, BLOCK_WIDTH, BLOCK_HEIGHT);
					}
					// draw ball
					g2d.setColor(Color.white);
					g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);

					// draw bar
					g2d.setColor(Color.white);
					g2d.fillRect(bar.x, bar.y, BAR_WIDTH, BAR_HEIGHT);
				}
				// draw Score
				g2d.setColor(Color.white);
				g2d.setFont(new Font("TimesRoman", Font.BOLD, 20));
				g2d.drawString("Score : " + score, (CANVAS_WIDTH / 2) - 50, 20);

				// draw Finish
				if (isGameFinish)
				{
					g2d.setColor(Color.red);
					g2d.drawString("Game Finished", (CANVAS_WIDTH / 2) - 65, 50);
				}
			}
		}

		public MyFrame(String Title)
		{
			super(Title);
			this.setVisible(true);
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
			this.setLocation(1000, 400);
			this.setLayout(new BorderLayout());
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			initData();

			myPanel = new MyPanel();
			this.add(myPanel);
			this.addKeyListener(new KeyHandler());

			startTimer();
		}

		public void initData()
		{
			for (int i = 0; i < BLOCK_ROWS; i++)
			{
				for (int j = 0; j < BLOCK_COLUMNS; j++)
				{
					blocks[i][j] = new Block();
					blocks[i][j].x = BLOCK_WIDTH * j + BLOCK_GAP * j;
					blocks[i][j].y = BLOCK_HEIGHT * i + BLOCK_GAP * i + 100;
					blocks[i][j].color = 4 - i;
				}
			}
		}

		public void startTimer()
		{
			timer = new Timer(20, new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					movement();
					checkCollision();
					checkCollisionBlock();
					myPanel.repaint();

					isGameFinish();
				}
			});
			timer.start();
		}

		public void isGameFinish()
		{
			int cnt = 0;
			for (int i = 0; i < BLOCK_ROWS; i++)
			{
				for (int j = 0; j < BLOCK_COLUMNS; j++)
				{
					if (blocks[i][j].isHidden)
						cnt++;
				}
			}
			if (cnt == BLOCK_ROWS * BLOCK_COLUMNS)
				isGameFinish = true;
		}

		public void movement()
		{
			if (bar.x > barXTarget && bar.x > 10)
			{ // go left
				bar.x -= 5;
			}
			else if (bar.x < barXTarget && bar.x < CANVAS_WIDTH - BAR_WIDTH - 25)
			{ // go right
				bar.x += 5;
			}

			if (dir == 0)
			{ // 0 : Up-Right
				ball.x += ballSpeed;
				ball.y -= ballSpeed;
			}
			else if (dir == 1)
			{ // 1 : Down-Right
				ball.x += ballSpeed;
				ball.y += ballSpeed;
			}
			else if (dir == 2)
			{ // 2 : Up-Left
				ball.x -= ballSpeed;
				ball.y -= ballSpeed;
			}
			else if (dir == 3)
			{ // 3 : Down-Left
				ball.x -= ballSpeed;
				ball.y += ballSpeed;
			}
		}

		public boolean duplRect(Rectangle rect1, Rectangle rect2)
		{
			return rect1.intersects(rect2); // check two Rect is Duplicated!
		}

		// check collision to wall and bar
		public void checkCollision()
		{
			switch (dir)
			{
			case 0:
			{ // 0 : Up-Right

				if (ball.y < 0)
				{ // wall upper
					dir = 1;
				}
				if (ball.x > CANVAS_WIDTH - BALL_WIDTH - 20)
				{ // wall right
					dir = 2;
				}
				break;
			}
			case 1:
			{ // 1 : Down-Right

				// Wall
				if (ball.y > CANVAS_HEIGHT - BALL_HEIGHT - BALL_HEIGHT)
				{ // wall bottom
					dir = 0;

					// game reset
					dir = 0;
					ball.x = bar.x + BAR_WIDTH / 2 - BALL_WIDTH / 2;
					ball.y = bar.y - BALL_HEIGHT;
					score = 0;
				}
				if (ball.x > CANVAS_WIDTH - BALL_WIDTH)
				{ // wall right
					dir = 3;
				}
				// Bar
				if (ball.getBottomCenter().y >= bar.y)
				{
					if (duplRect(new Rectangle(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT),
							new Rectangle(bar.x, bar.y, bar.width, bar.height)))
					{
						dir = 0;
					}
				}
				break;
			}
			case 2:
			{ // 2 : Up-Left

				// Wall
				if (ball.y < 0)
				{ // wall upper
					dir = 3;
				}
				if (ball.x < 0)
				{ // wall left
					dir = 0;
				}
				// Bar - none
				break;
			}
			case 3:
			{ // 3 : Down-Left

				// Wall
				if (ball.y > CANVAS_HEIGHT - BALL_HEIGHT - BALL_HEIGHT)
				{ // wall bottom
					dir = 2;

					// game reset
					dir = 0;
					ball.x = bar.x + BAR_WIDTH / 2 - BALL_WIDTH / 2;
					ball.y = bar.y - BALL_HEIGHT;
					score = 0;
				}
				if (ball.x < 0)
				{ // wall left
					dir = 1;
				}
				// Bar
				if (ball.getBottomCenter().y >= bar.y)
				{
					if (duplRect(new Rectangle(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT),
							new Rectangle(bar.x, bar.y, bar.width, bar.height)))
					{
						dir = 2;
					}
				}
				break;
			}
			}
		}

		public void checkCollisionBlock()
		{

			for (int i = 0; i < BLOCK_ROWS; i++)
			{
				for (int j = 0; j < BLOCK_COLUMNS; j++)
				{
					Block block = blocks[i][j];
					if (block.isHidden == false)
					{
						if (dir == 0)
						{ // 0 : Up-Right
							if (duplRect(new Rectangle(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT),
									new Rectangle(block.x, block.y, block.width, block.height)))
							{
								if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2)
									// block bottom collision
									dir = 1;
								
								else
									// block left collision
									dir = 2;

								block.isHidden = true;
								addScore(block);
							}
						}
						else if (dir == 1)
						{ // 1 : Down-Rigth
							if (duplRect(new Rectangle(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT),
									new Rectangle(block.x, block.y, block.width, block.height)))
							{
								if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2)
									// block top collision
									dir = 0;

								else
									// block left collision
									dir = 3;

								block.isHidden = true;
								addScore(block);
							}
						}
						else if (dir == 2)
						{ // 2 : Up-Left
							if (duplRect(new Rectangle(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT),
									new Rectangle(block.x, block.y, block.width, block.height)))
							{
								if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2)
									// block bottom collision
									dir = 3;

								else
								{
									// block right collision
									dir = 0;
								}
								block.isHidden = true;
								addScore(block);
							}
						}
						else if (dir == 3)
						{ // 3 : Down-Left
							if (duplRect(new Rectangle(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT),
									new Rectangle(block.x, block.y, block.width, block.height)))
							{
								if (ball.x > block.x + 2 && ball.getRightCenter().x <= block.x + block.width - 2)
								{
									// block top collision
									dir = 2;
								}
								else
								{
									// block right collision
									dir = 1;
								}
								block.isHidden = true;
								addScore(block);
							}
						}
					}
				}
			}
		}
		
		public void addScore(Block block) {
			if (block.color == 0)
			{
				score += 10;
			}
			else if (block.color == 1)
			{
				score += 20;
			}
			else if (block.color == 2)
			{
				score += 30;
			}
			else if (block.color == 3)
			{
				score += 40;
			}
			else if (block.color == 4)
			{
				score += 50;
			}
		}

		public class KeyHandler implements KeyListener
		{
			HashSet<Integer> keyValue = new HashSet<Integer>();
			Timer tempTimer;

			public KeyHandler()
			{
				tempTimer = new Timer(20, new ActionListener()
				{ // 20ms마다 액션 이벤트 발생
					@Override
					public void actionPerformed(ActionEvent arg0) // 20ms마다 발생한 액션 이벤트 처리
					{
						if (!keyValue.isEmpty())
						{
							Iterator<Integer> i = keyValue.iterator();
							int n = 0;
							while (i.hasNext())
							{
								n = i.next();
								if (n == KeyEvent.VK_LEFT)
								{
									barXTarget -= 5;

									if (bar.x < barXTarget)
									{ // repeate key pressed...
										barXTarget = bar.x;
									}
								}
								else if (n == KeyEvent.VK_RIGHT)
								{
									barXTarget += 5;

									if (bar.x > barXTarget)
									{ // repeate key pressed...
										barXTarget = bar.x;
									}
								}
								myPanel.repaint();
							}
						}
						else
						{
							tempTimer.stop();
						}
					}
				});
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				keyValue.add(e.getKeyCode());
				if (!tempTimer.isRunning())
					tempTimer.start();
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				keyValue.remove(e.getKeyCode());

			}

			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO 자동 생성된 메소드 스텁

			}

		}

	}

	public static void main(String[] args)
	{
		new MyFrame("Block Game");
	}
}