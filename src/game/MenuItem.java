package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Draws an individual MenuItem on a given JPanel
 * 
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class MenuItem
{
	private String text;
	private Menu menu;
	private LevelSelector levelSelector;
	private GameOver gameOver;
	private GamePanel gamePanel;
	private Location location;
	private int width, height;
	private static final int CENTER_RATIO = 5;
	private int length;
	private int tier;
	private int shift;
	private boolean enabled;
	private boolean isLevel;
	private boolean isPressed;
	private StatusBar statusBar;

	/**
	 * Creates a Rectangle object to be used in Menu class
	 * 
	 * @param menu
	 * @param text
	 * @param location
	 * @param width
	 * @param height
	 * @param tier
	 */
	public MenuItem(Menu menu, String text, Location location, int width,
			int height, int tier)
	{
		this.menu = menu;
		this.text = text;
		this.location = location;
		this.width = width;
		this.height = height;
		this.tier = tier;
		this.length = text.length();
		if (this.text.charAt(1) == 'u')
			this.length += (menu.getPlayerName().length() + 3);
		this.setEnabled(true);
	}

	/**
	 * Creates a Rectangle object to be used in GamePanel
	 * @param gamePanel
	 * @param text
	 * @param location
	 * @param width
	 * @param height
	 * @param tier
	 */
	public MenuItem(GamePanel gamePanel, String text, Location location,
			int width, int height, int tier)
	{
		this.gamePanel = gamePanel;
		this.text = text;
		this.location = location;
		this.width = width;
		this.height = height;
		this.tier = tier;
		this.shift = 0;
		this.length = text.length();
		this.setEnabled(true);
	}

	/**
	 * Creates a Rectangle object to be used in GamePanel
	 * @param gamePanel
	 * @param text
	 * @param location
	 * @param width
	 * @param height
	 * @param tier
	 * @param shift
	 */
	public MenuItem(GamePanel gamePanel, String text, Location location,
			int width, int height, int tier, int shift)
	{
		this.gamePanel = gamePanel;
		this.text = text;
		this.location = location;
		this.width = width;
		this.height = height;
		this.tier = tier;
		this.shift = shift;
		this.length = text.length();
		this.setEnabled(true);
	}

	/**
	 * Creates a Rectangle object to be used in LevelSelector
	 * @param levelSelector
	 * @param text
	 * @param location
	 * @param width
	 * @param height
	 * @param tier
	 */
	public MenuItem(LevelSelector levelSelector, String stage,
			Location location, int width,
			int height, int tier, boolean state)
	{
		this.levelSelector = levelSelector;
		this.text = stage;
		this.location = location;
		this.width = width;
		this.height = height;
		this.tier = tier;
		this.length = this.text.length();
		this.enabled = state;
		this.isLevel = false;
		this.isPressed = false;
	}

	/**
	 * Creates a Rectangle object to be used in LevelSelector
	 * @param levelSelector
	 * @param stage
	 * @param level
	 * @param location
	 * @param width
	 * @param height
	 * @param state
	 */
	public MenuItem(LevelSelector levelSelector, int stage, int level,
			Location location, int width,
			int height, boolean state)
	{
		this.levelSelector = levelSelector;
		this.location = location;
		this.width = width;
		this.height = height;
		this.enabled = state;
		this.isLevel = true;
		this.tier = (stage * LevelSelector.LEVELS_PER_STAGE) + level + 1;
		this.text = "Level " + String.valueOf(tier);
		this.length = 7;
		System.out.println(text + "\nEnabled: " + this.enabled);
	}

	/**
	 * Creates a Rectangle object to be used in GameOver class
	 * @param gameOver
	 * @param text
	 * @param location
	 * @param width
	 * @param height
	 * @param tier
	 */
	public MenuItem(GameOver gameOver, String text, Location location,
			int width,
			int height, int tier)
	{
		this.gameOver = gameOver;
		this.text = text;
		this.location = location;
		this.width = width;
		this.height = height;
		this.tier = tier;
		this.length = text.length();
		this.shift = 0;
		this.setEnabled(true);

		if (this.text.equals(GameOver.titles[1]))
		{
			this.shift = 5;
		}
	}

	/**
	 * Creates a Rectangle object to be used in StatusBar
	 * @param statusBar
	 * @param text
	 * @param location
	 * @param width
	 * @param height
	 */
	public MenuItem(StatusBar statusBar, String text, Location location,
			int width,
			int height)
	{
		this.statusBar = statusBar;
		this.text = text;
		this.location = location;
		this.width = width;
		this.height = height;
		this.length = text.length();
		this.setEnabled(true);
	}

	/**
	 * Draws itself on the JPanel
	 * @param g The Graphics object to change
	 */
	public synchronized void draw(Graphics g)
	{
		// Specific draw method for each JPanel type due to format variation
		if (this.menu != null)
		{
			g.drawImage(menu.images[0], this.location.x, this.location.y,
					this.width, this.height, null);
			g.setFont(new Font("Calibri", Font.BOLD, 36));
			g.setColor(Color.BLACK);
			g.drawString(this.text, (this.location.x)
					+ ((40 - length) * CENTER_RATIO), (this.location.y)
					+ (this.height - 25));
		}
		else if (this.levelSelector != null)
		{
			if (!this.isLevel)
			{
				if (!this.isPressed)
				{
					g.setColor(Color.BLUE);
				}
				else
				{
					g.setColor(Color.DARK_GRAY);
				}
				g.fillRect(this.location.x, this.location.y, this.width,
						this.height);
				g.setColor(Color.WHITE);
				g.setFont(new Font("Calibri", Font.BOLD, 36));
				g.drawString(this.text, this.location.x + 25,
						this.location.y + 45);
			}
			else
			{
				if (this.enabled)
					g.setColor(Color.BLUE);
				else
					g.setColor(Color.DARK_GRAY);
				g.fillRect(this.location.x, this.location.y, this.width,
						this.height);
				g.setColor(Color.WHITE);
				g.setFont(new Font("Calibri", Font.BOLD, 36));
				g.drawString(
						this.text,
						(this.location.x + 50 + ((this.length - 8) * CENTER_RATIO)),
						this.location.y + 45);
			}
		}
		else if (this.gameOver != null)
		{
			g.setColor(Color.DARK_GRAY);
			g.fillRect(this.location.x, this.location.y, this.width,
					this.height);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Calibri", Font.BOLD, 36));
			g.drawString(this.text, this.location.x + 20 - this.shift,
					this.location.y + 35);
		}
		else if (this.gamePanel != null)
		{
			System.out.println("Entered draw() in MenuItem for GamePanel");
			g.setColor(Color.DARK_GRAY);
			g.fillRect(this.location.x, this.location.y, this.width,
					this.height);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Calibri", Font.BOLD, 36));
			g.drawString(this.text, this.location.x + 40 - this.shift,
					this.location.y + 45);
		}
		else
		{
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(this.location.x, this.location.y, this.width,
					this.height);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Calibri", Font.BOLD, 24));
			g.drawString(this.text, this.location.x + 21, this.location.y + 21);
		}

		// Draw a border around any type of MenuItem
		g.setColor(Color.BLACK);
		g.drawRect(this.location.x - 1, this.location.y - 1,
				this.width + 1, this.height + 1);
	}

	/**
	 * Checks if the point is within the Rectangle
	 * @param x The x position
	 * @param y The y position
	 * @return Whether or not the point is within
	 */
	public boolean isWithin(int x, int y)
	{
		if (x >= this.location.x && x <= this.location.x + this.width
				&& y >= this.location.y && y <= this.location.y + this.height)
			return true;
		else
			return false;
	}

	/**
	 * Changes the state to the given boolean
	 * @param state The boolean to set the state to
	 */
	public void setEnabled(boolean state)
	{
		this.enabled = state;
	}

	/**
	 * Setter for isPressed
	 * @param state The boolean to set isPressed to
	 */
	public void setPressed(boolean state)
	{
		this.isPressed = state;
	}

	/**
	 * Called when clicked in JPanel, performs respective action
	 */
	public synchronized void clicked()
	{
		System.out.println("Entered clicked in MenuItem(): " + this.text);

		if (this.menu != null)
		{
			System.out.println("Entered Menu");
			if (this.tier == 0)
			{
				return;
			}
			if (this.tier == 1)
			{
				menu.switchPlayer();
			}
			else if (this.tier == 2)
			{
				menu.newGame();
			}
			else if (this.tier == 3)
			{
				menu.instructions();
			}
			else if (this.tier == 4)
			{
				menu.credits();
			}
		}
		else if (this.gamePanel != null)
		{
			System.out.println("Entered GamePanel");
			if (this.tier == 0)
			{
				this.gamePanel.cancelMenu();
			}
			else if (this.tier == 1)
			{
				this.gamePanel.retry();
			}
			else if (this.tier == 2)
			{
				this.gamePanel.returnToMainMenu();
			}
			else if (this.tier == 3)
			{
				this.gamePanel.toLevelSelector();
			}
			else if (this.tier == 4)
			{
				this.gamePanel.showHelpMenu();
			}
		}
		else if (this.levelSelector != null)
		{
			System.out.println("Entered LevelSelector");
			if (!this.isLevel)
			{
				this.setEnabled(true);
				this.setPressed(true);
			}
			else if (this.enabled)
			{
				this.levelSelector.newGame(this.tier);
			}
		}
		else if (this.gameOver != null)
		{
			System.out.println("Entered GamePanel");
			if (this.tier == 0)
			{
				this.gameOver.retry();
			}
			else if (this.tier == 1)
			{
				this.gameOver.returnToMainMenu();
			}
			else if (this.tier == 2)
			{
				this.gameOver.toLevelSelector();
			}
		}
		else
		{
			this.statusBar.openMenu();
		}
	}

	/**
	 * Change the displaying text
	 * @param text The new text to display
	 */
	void changeText(String text)
	{
		this.length += (text.length() - this.text.length());
		this.text = text;
	}

	/**
	 * Returns whether or not this is a level
	 * @return whether or not this is a level
	 */
	public boolean isLevel()
	{
		return this.isLevel;
	}

	/**
	 * Returns whether or not this is pressed
	 * @return whether or not this is pressed
	 */
	public boolean isPressed()
	{
		return this.isPressed;
	}

	/**
	 * Closes the MenuItem
	 */
	public void closed()
	{
		this.setEnabled(false);
		this.setPressed(false);
	}

}
