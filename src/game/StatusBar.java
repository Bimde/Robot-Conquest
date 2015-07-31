package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Displays important information to the User during a level (i.e Robot health)
 * 
 * @author Bimesh De Silva
 * @version January 2015
 */
public class StatusBar extends JPanel implements ActionListener, MouseListener
{
	private Game game;
	private GamePanel gamePanel;
	private int barWidth;
	private int height, width;
	private Robot robot;
	private int robotHealthPercentage;
	private int[] rowPositions;
	private final int delay = 1000;
	private static final int SMALL_HEIGHT = 50;
	private static final int LARGE_HEIGHT = 100;
	private static final int MAX_GAME_TIME = 4 * 60;
	public static final int MARGIN = 20;
	private static final String MENU_TEXT = "Open Menu";
	private MenuItem menu;
	private String playerName;
	private int gameTimeSeconds;
	private int gameTimeMinutes;
	private int level;
	private String levelText;
	private Rectangle timeBox;
	private Timer timer;
	private String time = "";
	private boolean paused;

	/**
	 * Constructor for StatusBar
	 * @param game The current JFrame
	 * @param gamePanel The current main JPanel
	 */
	public StatusBar(Game game, GamePanel gamePanel)
	{
		super();
		this.game = game;
		this.gamePanel = gamePanel;
	}

	/**
	 * Sets up important variables
	 */
	public void loadData()
	{
		// Get data from the Main JPanel
		this.robot = gamePanel.getRobot();
		this.height = gamePanel.getWidth();
		this.width = this.height / 3;
		this.barWidth = this.width - (MARGIN * 2);
		this.robotHealthPercentage = (robot.getHealth() * 100)
				/ robot.MAX_HEALTH;

		// Sets up size and layout (y positions for menu buttons)
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.setMinimumSize(new Dimension(this.width, this.height));
		this.rowPositions = new int[] { MARGIN, LARGE_HEIGHT, SMALL_HEIGHT,
				SMALL_HEIGHT, SMALL_HEIGHT, SMALL_HEIGHT, LARGE_HEIGHT,
				SMALL_HEIGHT,
				LARGE_HEIGHT - 20, SMALL_HEIGHT, SMALL_HEIGHT };
		for (int row = 1; row < this.rowPositions.length; row++)
		{
			this.rowPositions[row] += this.rowPositions[row - 1];
		}

		// Set up fields
		this.menu = new MenuItem(this, MENU_TEXT, new Location(0, 0,
				this.rowPositions[0] + 10, MARGIN * 2
				), this.width - (MARGIN * 4), SMALL_HEIGHT - 20);
		this.timeBox = new Rectangle(0, this.rowPositions[6], this.width, 100);
		this.level = this.gamePanel.getLevel();
		this.levelText = "Current Level: " + this.level;
		this.playerName = this.game.getCurrentPlayer();
		if (this.playerName.charAt(0) == '*')
			this.playerName = this.playerName.substring(1);

		// Listen for MouseEvents
		this.addMouseListener(this);
	}

	/**
	 * Called to start the Timer and start doing damage to Robot
	 */
	public void start()
	{
		this.gameTimeMinutes = MAX_GAME_TIME / 60;
		this.gameTimeSeconds = 0;
		this.paused = false;
		this.timer = new Timer(delay, this);
		this.timer.start();
	}

	/**
	 * Return the current time left
	 * @return the current time left
	 */
	public int getTimeLeft()
	{
		return ((this.gameTimeMinutes * 60) + this.gameTimeSeconds);
	}

	/**
	 * Updates StatusBar information with a new time and potential health
	 * changes
	 */
	public void update()
	{
		if (gameTimeMinutes >= 0)
		{
			int health = robot.getHealth();
			this.robotHealthPercentage = (health * 100)
					/ robot.MAX_HEALTH;
			if (this.robotHealthPercentage == 0 && health != 0)
			{
				this.robotHealthPercentage = 1;
			}

			this.repaint();

			// Health drain on Robot (After repaint() because it prevents full
			// health image otherwise)
			this.robot.loseHealth(gamePanel.HEALTH_DRAIN);
		}
		else
		{
			gamePanel.setState(-2);
			gamePanel.endGame();
		}
	}

	/**
	 * Specifically called to update the time variable
	 */
	private void updateTime()
	{
		if (!paused)
		{
			// Adjust remaining time
			if (this.gameTimeSeconds <= 0)
			{
				this.gameTimeMinutes--;
				this.gameTimeSeconds = 59;
			}
			else
				this.gameTimeSeconds--;
			this.time = String.format("%02d:%02d", this.gameTimeMinutes,
					this.gameTimeSeconds);
			this.update();
		}
	}

	/**
	 * Ends calling actionPerformed() method
	 */
	public void gameOver()
	{
		this.timer.stop();
	}

	/**
	 * Draws the StatusBar objects fields on the JPanel
	 * @Override The built in paintComponent() method
	 */
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);

		// Draw the game time remaining
		g.setColor(Color.BLACK);
		g.setFont(new Font("Calibri", Font.BOLD, 40));
		g.drawString(this.time, MARGIN * 3 + 5, this.rowPositions[5] + 20);

		// Draw Heath bar
		g.setColor(Color.BLACK);
		g.drawRect(MARGIN - 1, this.rowPositions[7] - 1, this.barWidth + 1, 36);
		g.drawRect(MARGIN - 2, this.rowPositions[7] - 2, this.barWidth + 3, 38);
		g.setColor(Color.RED);
		g.fillRect(MARGIN, this.rowPositions[7], this.barWidth, 35);
		g.setColor(Color.GREEN);
		g.fillRect(MARGIN, this.rowPositions[7],
				(int) ((this.robotHealthPercentage / 100.0) * this.barWidth),
				35);

		// Draw Text
		g.setColor(Color.BLACK);
		g.setFont(new Font("Courier New", Font.BOLD, 20));
		g.drawString(this.playerName, 20, this.rowPositions[1]);
		g.drawString(this.levelText, 20, this.rowPositions[2]);

		g.setFont(new Font("Calibri", Font.BOLD, 26));
		g.drawString("Time Remaining", 20, this.rowPositions[4]);
		g.drawString("Health", 30, this.rowPositions[6]);

		g.setFont(new Font("Courier New", Font.BOLD, 20));
		g.drawString("Keys Required: " + this.gamePanel.getRequiredKeys(), 20,
				this.rowPositions[9]);
		g.drawString("Keys Found: " + this.gamePanel.getCurrentKeys(), 20,
				this.rowPositions[10]);

		// Draw Separators
		g.fillRect(0, this.rowPositions[3], this.width, 3);
		g.fillRect(0, this.rowPositions[8], this.width, 3);

		// Draw menu rectangle (acting as a button)
		this.menu.draw(g);
	}

	/**
	 * Repaint only the area of the countdown timer
	 */
	public synchronized void repaintHealthBar()
	{
		this.paintImmediately(timeBox);
	}

	/**
	 * Called every Timer tick to update variables
	 * @Override Abstract actionPerformed() method in ActionListener
	 */
	public void actionPerformed(ActionEvent e)
	{
		this.updateTime();
	}

	/**
	 * Open's Menu in the GamePanel JPanel
	 */
	public void openMenu()
	{
		this.paused = true;
		gamePanel.drawMenu();
	}

	/**
	 * Called when Menu is closes to resume Timer events
	 */
	public void menuClosed()
	{
		this.paused = false;
	}

	/**
	 * Receives MouseEvents and checks if Menu button was clicked
	 * @Override Abstract MouseClicked() method in MouseListener
	 */
	public void mouseClicked(MouseEvent e)
	{
		if (this.menu.isWithin(e.getX(), e.getY()))
			this.menu.clicked();
	}

	// Filler methods for MouseListener
	@Override
	public void mouseEntered(MouseEvent e)
	{
		return;
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		return;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		return;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		return;
	}

}
