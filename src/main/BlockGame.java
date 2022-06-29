package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import main.BlockGame2.MyFrame;
import main.BlockGame2.MyFrame.Block;
import main.BlockGame2.MyFrame.MyPanel;

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
		static int ballspeed = 5;
		static boolean isGameFinish = false;

		static class Block
		{
			int x = 0;
			int y = 0;
			int width = BLOCK_WIDTH;
			int height = BLOCK_HEIGHT;
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
				
			}
		}
	}

	public static void main(String[] args)
	{
		
	}
}