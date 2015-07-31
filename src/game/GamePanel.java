package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The main JPanel of the game, called when user starts playing a new level
 * Handles all game play and drawing (except the robot, which draws itself)
 * 
 * @author Bimesh De Silva
 * @version January 2014
 *
 */
public class GamePanel extends JPanel implements KeyListener, ActionListener,
		MouseListener
{
	private int width;
	private Game game;
	public Square[][] grid;
	private int state;
	private boolean isBattling;
	private Robot robot;
	private Location robotPos;
	private Enemy currentEnemy;
	private static boolean[] keysPressed;
	private static final String[] titles = { "        Resume",
			"        Restart", "    Main Menu",
			"  Level Selector", "           Help" };
	private final int DELAY = 100;
	public final int HEALTH_DRAIN = 2;
	public final int HEALING_BOOST = 100 / (1000 / DELAY);
	private int requiredKeys;
	private int currentKeys;
	private Location exitLocation;
	private StatusBar statusBar;
	private Timer timer;
	private int level;
	private boolean drawMenu;

	// Stores all of the required images
	private final int NO_OF_IMAGES = 12;
	public BufferedImage[] images;
	public BufferedImage[] robotImages;
	private boolean isAttacking;
	private int[] rowPositions;
	private MenuItem[] rectangles;
	private int buttonWidth;
	private boolean paused;
	private boolean showHelp;
	private BufferedImage help;

	/**
	 * GamePanel constructor, creates and sets up main JPanel of the game
	 * 
	 * @param game A reference to the main JFrame
	 */
	public GamePanel(Game game)
	{
		// Sets local variables to required values
		this.game = game;
		// this.width = Game.SQUARE_WIDTH * Game.NO_OF_ROWS;
		// this.drawItem = false;

		// Loads the images required to draw the game components
		this.loadImages();
	}

	/**
	 * Loads the menu buttons for the in-game menu
	 */
	private void loadRectangles()
	{
		this.rowPositions = new int[] { 50, Menu.BUTTON_HEIGHT,
				Menu.MARGIN / Menu.RATIO,
				Menu.BUTTON_HEIGHT, Menu.MARGIN / Menu.RATIO,
				Menu.BUTTON_HEIGHT, Menu.MARGIN / Menu.RATIO,
				Menu.BUTTON_HEIGHT, Menu.MARGIN / Menu.RATIO,
				Menu.BUTTON_HEIGHT, 50 };
		for (int row = 1; row < this.rowPositions.length; row++)
		{
			this.rowPositions[row] += this.rowPositions[row - 1];
		}

		// Determine the x and y positions of the MenuItem objects with the
		// pre-determined row positions
		this.rectangles = new MenuItem[titles.length];
		this.buttonWidth = this.width - (Menu.MARGIN + Menu.MARGIN) + 100;
		for (int rectangle = 0; rectangle < titles.length; rectangle++)
		{
			Location location = new Location(0, 0,
					this.rowPositions[(rectangle * 2) + 1], Menu.MARGIN - 50);
			this.rectangles[rectangle] = new MenuItem(this,
					titles[rectangle], location, this.buttonWidth,
					Menu.BUTTON_HEIGHT, rectangle);
		}
	}

	/**
	 * Loads images required to draw the game components
	 */
	private void loadImages()
	{
		// Loads the images into a new BufferedImage array
		this.images = new BufferedImage[this.NO_OF_IMAGES];
		for (int picture = 0; picture < this.NO_OF_IMAGES; picture++)
		{
			try
			{
				this.images[picture] = ImageIO.read(new File("Image" + picture
						+ ".png"));
			}
			catch (IOException e)
			{
				System.err.println("Loading image #" + picture
						+ " - loadImages() in Main class");
			}
		}
		try
		{
			this.help = ImageIO.read(new File("MenuImage2.png"));
		}
		catch (IOException e)
		{
			System.out.println("Loading MenuImage2.png in loadImages() in GamePanel");
		}
	}

	/**
	 * Calls Game to restart the current level
	 */
	public void retry()
	{
		this.setVisible(false);
		this.setEnabled(false);
		this.game.remove(this);
		this.game.refresh();
		this.game.newLevel(this.level);
	}

	/**
	 * Calls Game to return the main menu
	 */
	public void returnToMainMenu()
	{
		this.setVisible(false);
		this.setEnabled(false);
		this.game.remove(this);
		this.game.refresh();
		this.game.newMenu();
	}

	/**
	 * Calls Game to open the level selector
	 */
	public void toLevelSelector()
	{
		this.setVisible(false);
		this.setEnabled(false);
		this.game.remove(this);
		this.game.refresh();
		this.game.newLevelSelector();
	}

	/**
	 * Returns the Image from the given array position
	 * @param ref The array position of the desired image
	 * @return The Image at the given array position
	 */
	public Image getImage(int ref)
	{
		return images[ref];
	}

	/**
	 * Getter for the current level being played
	 * @return The current level
	 */
	public int getLevel()
	{
		return this.level;
	}

	/**
	 * Allows Game to add required data to this object
	 * 
	 * @param robot The Robot object previously created by game
	 * @param statusBar The StatusBar that displays important information
	 */
	public void addGameData(Robot robot, StatusBar statusBar, int level)
	{
		this.state = 0;
		this.level = level;
		this.robot = robot;
		this.statusBar = statusBar;
		this.grid = game.getGrid();
		this.paused = false;
		this.showHelp = false;
		this.requiredKeys = game.getNoOfKeys();
		keysPressed = new boolean[4];
		this.drawMenu = false;
		this.width = Game.SQUARE_WIDTH * Game.NO_OF_ROWS;
		this.setEnabled(true);
		this.setPreferredSize(new Dimension(this.width, this.width));
		this.setMinimumSize(new Dimension(this.width, this.width));

		// Loads MenuItem objects required for menu
		this.loadRectangles();

		this.setFocusable(true);
		this.addKeyListener(this);
		this.setDoubleBuffered(true);
	}

	/**
	 * Allows other objects to get the current Robot object
	 * 
	 * @return The current Robot
	 */
	public Robot getRobot()
	{
		return this.robot;
	}

	@Override
	/**
	 * Overrides the built in paintComponent method
	 * Draws the game play based on the current state of the game
	 * States: 	0 = normal
	 * 			2 = in battle
	 * 			1 = Game Won
	 * 			-1 = Game Lost
	 * 			-2 = Time Ran Out
	 * @param the Graphics object to change
	 */
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);

		// Draw the game grid if the drawMenu flag isn't in place
		if (!this.drawMenu)
		{

			Image image = this.images[0];

			// Draws the game play grid if the state is correct
			if (this.state == 0 || this.state == 2)
			{
				// Draw the background
				g.drawImage(image, 0, 0, this.width, this.width, this.game);

				// Draw all stationary items the cycling through all grid
				// squares
				for (int row = 0; row < Game.NO_OF_ROWS; row++)
				{
					for (int column = 0; column < Game.NO_OF_ROWS; column++)
					{
						char overlay = this.grid[row][column].getOverlay();
						if (this.grid[row][column].itemImgRef != 0)
						{
							if (overlay != game.BOSS
									&& overlay != game.HIDDEN_ENEMY
									&& overlay != game.ENEMY)
							{
								image = this.images[this.grid[row][column].itemImgRef];
								g.drawImage(image, column * Game.SQUARE_WIDTH,
										row * Game.SQUARE_WIDTH,
										Game.SQUARE_WIDTH,
										Game.SQUARE_WIDTH, this.game);
							}
							else
							{
								this.grid[row][column].getEnemy().draw(g);
							}

						}
					}
				}

				// Calls Robot object to draw it self on the given Graphics
				// object
				this.robot.draw(g);
			}
		}
		// If the flag is up, draw the in-game menu
		else
		{
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(10, 10, this.width - 20, this.width - 20);

			if (!showHelp)
			{
				for (int item = 0; item < rectangles.length; item++)
				{
					this.rectangles[item].draw(g);
				}
			}
			else
			{
				g.drawImage(this.help, 5, 5, this.width-10, this.width-10, this.game);
			}
		}
	}

	/**
	 * Allows Game to set the exit location when creating the grid of Squares
	 * 
	 * @param row The row position on the Square object array
	 * @param column The column position on the Square object array
	 */
	public void setExitLocation(int row, int column)
	{
		this.exitLocation = new Location(row, column);
	}

	/**
	 * Creates a new Timer object
	 */
	public void startGame()
	{
		this.timer = new Timer(DELAY, this);
		this.timer.start();
		this.statusBar.start();
		this.currentKeys = 0;
		this.isBattling = false;
		this.isAttacking = false;
	}

	/**
	 * Main game play method, called with each timer tick Checks for any input
	 * by user, calls repaint() if any changes are made to objects
	 */
	private void process()
	{
		robotPos = robot.getLocation();
		if (this.grid[robotPos.row][robotPos.column].isStartHere())
		{
			this.robot.healthBoost(this.HEALING_BOOST);
		}

		// Only check changes to grid if user moved the Robot
		if (this.move())
		{
			this.paintImmediately(0, 0, this.width, this.width);
			if (this.checkMove())
				this.repaint();
		}
	}

	/**
	 * Using the health and damage parameters of the Robot and Enemy objects,
	 * perform one "trade" of health between the two objects.
	 * 
	 * @deprecated Replaced with Timer in Enemy object and actionPerformed for
	 *             user's Robot object
	 * @param square The square of which the enemy is in
	 */
	private void battle(Square square)
	{
		// Sets up required variables
		Enemy enemy = square.getEnemy();
		System.out.println("Start battle!");
		this.isBattling = true;
		int robotDmg = this.robot.getDamage();
		int enemyDmg = enemy.getDamage();
		System.out.println("Robot Health: " + this.robot.getHealth());
		System.out.println("Enemy Health: " + enemy.getHealth());

		// Performs the "trade" of health using the given damage parameters
		robot.loseHealth(enemyDmg);
		enemy.loseHealth(robotDmg);

		// Checks for any end results of the battle
		if (this.robot.isDisabled())
		{
			this.isBattling = false;
			this.state = -1;
			this.endGame();
		}
		else if (enemy.isDisabled())
		{
			this.isBattling = false;
			System.out.println("EPIC SOUND \n AND STARS");
			square.enemyDefeated();
		}
	}

	/**
	 * Returns the width of the JPanel
	 * 
	 * @return the width of the JPanel
	 */
	public int getWidth()
	{
		return this.width;
	}

	public synchronized void repaintHealthBar()
	{
		this.statusBar.repaintHealthBar();
	}

	/**
	 * Checks for item and calls the respective Square object's itemPickedUp
	 * method
	 * 
	 * @param row The row position on the Square object 2-D array
	 * @param column The column position on the Square object 2-D array
	 * @return
	 */
	private boolean pickUpItem(int row, int column)
	{
		// Only calls itemPickedUp if their is an item there
		if (this.grid[row][column].isItemHere())
		{
			this.itemPickedUp(this.grid[row][column].itemPickedUp());
			return true;
		}
		return false;
	}

	/**
	 * Determines the consequence of picking up the given item
	 * 
	 * @param item The item that was picked up
	 */
	public void itemPickedUp(Item item)
	{
		if (item.getType() == game.DAMAGE_BOOST)
		{
			int value = item.getValue();
			System.out.println("Damage Boost: " + value);
			this.robot.damageBoost(value);
		}
		else if (item.getType() == game.HEALTH_BOOST)
		{
			int value = item.getValue() * 5;
			System.out.println("Health Boost: " + value);
			this.robot.healthBoost(value);
		}
		else if (item.getType() == game.KEY)
		{
			System.out.println("Key Found!");
			this.keyFound();
			System.out.println("Number of Keys: " + this.currentKeys);
		}
		this.statusBar.update();
	}

	/**
	 * Called when a key Item is picked up, determines consequence
	 */
	private void keyFound()
	{
		this.currentKeys++;

		// Unlocks exit if the number of keys has reached the required amount
		if (this.currentKeys >= this.requiredKeys)
			this.exitUnlocked();
	}

	/**
	 * Called by keyFound method with the correct number of keys; Unlocks the
	 * exit by calling exitUnlocked in its respective location
	 */
	private void exitUnlocked()
	{
		this.grid[exitLocation.row][exitLocation.column].exitUnlocked();
	}

	/**
	 * Checks and reveals Item objects within 2 squares and calls the engage()
	 * method in any enemies are found within 1 grid square
	 * 
	 * @return whether or not any objects were revealed
	 */
	private boolean checkMove()
	{
		// Set up required variables and checks for Item on respective grid
		// square
		int row = robotPos.row;
		int column = robotPos.column;
		boolean revealed = false;
		if (this.pickUpItem(row, column))
			revealed = true;

		// Ends game if exit is underneath Robot
		if (this.grid[row][column].isExitHere())
		{
			this.state = 1;
			this.endGame();
		}

		// Reveals all items within 2 grid squares
		for (int addColumn = -2; addColumn <= 2; addColumn++)
		{
			for (int addRow = -2; addRow <= 2; addRow++)
			{
				row += addRow;
				column += addColumn;
				if (row >= 0 && column >= 0 && row <= 23 && column <= 23)
				{
					if (this.reveal(row, column))
						revealed = true;
				}
				row -= addRow;
				column -= addColumn;
			}
		}

		// Checks horizontally and vertically for an enemy within
		// 1 square and engages it (not diagonally)
		for (int change = -1; change <= 1; change++)
		{
			row += change;
			if (this.engage(row, column))
			{
				revealed = true;
				this.isBattling = true;
			}
			row -= change;
			column += change;
			if (this.engage(row, column))
			{
				revealed = true;
				this.isBattling = true;
			}
			column -= change;
		}

		// Code for additional diagonal checks
		// for (int addColumn = -1; addColumn <= 1; addColumn++)
		// {
		// for (int addRow = -1; addRow <= 1; addRow++)
		// {
		// row += addRow;
		// column += addColumn;
		// if(this.engage(row, column))
		// revealed = true;
		// row -= addRow;
		// column -= addColumn;
		// }
		// }

		return revealed;
	}

	/**
	 * Looks for an Enemy that is currently engaged and attack it
	 * 
	 * @return If an Enemy is currently engaged
	 */
	private boolean findBattle()
	{
		if (this.isBattling)
		{
			currentEnemy.loseHealth(robot.getDamage());
			return true;
		}

		return false;
	}

	/**
	 * End the current level by calling Game and reseting important variables
	 */
	synchronized void endGame()
	{
		statusBar.gameOver();
		timer.stop();
		game.gameOver(state);
		currentEnemy.dispose();
		this.setEnabled(false);
	}

	/**
	 * Looks for an an Enemy in the given position
	 * @param row The row to look at
	 * @param column The column to look at
	 * @return Whether or not an enemy was found
	 */
	private boolean reveal(int row, int column)
	{
		if (this.grid[row][column].isItemHere())
		{
			this.grid[row][column].itemDetected();
			return true;
		}
		if (this.grid[row][column].isEnemyHere())
		{
			this.grid[row][column].enemyDetected();
			return true;
		}
		return false;
	}

	/**
	 * Look for an engage in the given position and engage
	 * @param row The row that a potential Enemy is in
	 * @param column The row that a potential Enemy is in
	 * @return Whether or not an Enemy was found in the given position
	 */
	private boolean engage(int row, int column)
	{
		if (grid[row][column].isEnemyHere())
		{
			// this.battle(grid[row][column]);
			this.currentEnemy = grid[row][column].getEnemy();
			this.currentEnemy.startBattle(this.robot);
			this.isBattling = true;
			return true;
		}
		return false;
	}

	/**
	 * Looks for keys pressed by the user and moves the Robot
	 * @return Whether or not the Robot moved this cycle
	 */
	private boolean move()
	{
		boolean moved = false;

		// Only moves if the Robot isn't engaged to an Enemy
		// Changes moved boolean to true if any action is performed
		if (!this.isBattling)
		{
			int rowChange = 0;
			int columnChange = 0;
			Location original = robot.getLocation();
			if (keysPressed[0])
			{
				rowChange--;
				moved = true;
			}
			if (keysPressed[1])
			{
				columnChange++;
				moved = true;
			}
			if (keysPressed[2])
			{
				rowChange++;
				moved = true;
			}
			if (keysPressed[3])
			{
				columnChange--;
				moved = true;
			}

			// Only updates the Robot if their was a row or column change (i.e
			// if a user presses down and up there was no change so it wouldn't
			// enter this loop)
			if (moved && (rowChange != 0 || columnChange != 0))
			{
				int newRow = rowChange + original.row;
				int newColumn = columnChange + original.column;
				// Check if the new point is an available location
				robot.changeDirection(rowChange, columnChange);
				// if (this.grid[newRow][newColumn].isEmpty())
				// {
				// if (grid[newRow][newColumn].isDirectionHere())
				// {
				// if (robot.getDirection().equals(
				// grid[newRow][newColumn].passableDirection()))
				// this.robot.move(row, column);
				// }
				// else
				// this.robot.move(row, column);
				// this.repaint();
				// return true;
				// }
				// else
				// this.repaint();

				// Moves theRobot to the new location if possible
				if (this.grid[newRow][newColumn].isEmpty())
				{

					// Checks if the Robot is traveling in the correct direction
					if (this.grid[original.row][original.column]
							.isDirectionHere())
					{
						int directionDifference = Math.abs(this.robot
								.getDirection().number()
								- this.grid[original.row][original.column]
										.passableDirection().number());

						// Allows for a one direction difference (the 7 is there
						// for the North(0) to North-West(7) special case
						if (directionDifference <= 1
								|| directionDifference == 7)
							this.robot.move(rowChange, columnChange);
					}
					// If there isn't a direction marker, move as usual
					else
						this.robot.move(rowChange, columnChange);
				}
			}
		}
		return moved;
	}

	/**
	 * Getter for the required number of keys
	 * @return The required number of keys
	 */
	public int getRequiredKeys()
	{
		return this.requiredKeys;
	}

	/**
	 * Getter for the current number of keys
	 * @return The current number of keys acquired by the user
	 */
	public int getCurrentKeys()
	{
		return this.currentKeys;
	}

	/**
	 * Getter for the current state of the game
	 * @return The current state
	 */
	public int getState()
	{
		return this.state;
	}

	/**
	 * Setter for the game's state
	 * @param state The state to set the game in
	 */
	void setState(int state)
	{
		if (state >= -2 && state <= 2)
			this.state = state;
		System.out.println("State set to: " + this.state);
	}

	/**
	 * Receives KeyEvents from the JFrame; Changes the keysPressed array for use
	 * by the move() method
	 * @Override The KeyListener keyPressed() method
	 */
	public void keyPressed(KeyEvent e)
	{
		if (!paused)
		{
			int key = e.getKeyCode();

			// If the Robot is currently attacking, don't accept KeyEvents
			if (!isAttacking)
			{
				// Only accept movement KeyEvents if the Robot isn't engaged
				if (!this.isBattling)
				{
					if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
						keysPressed[0] = true;
					else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)
						keysPressed[1] = true;
					else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
						keysPressed[2] = true;
					else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
						keysPressed[3] = true;
				}
				// Only accept attacking KeyEvents if the Robot is engaged
				else if (key == KeyEvent.VK_F || key == KeyEvent.VK_SPACE)
				{
					// If there was a battle, repaint the JPanel
					if (this.findBattle())
					{
						this.repaint();
						this.statusBar.update();
					}
				}
			}
		}
	}

	/**
	 * Sets the attacking state of the Robot using the given boolean
	 * @param state Set the attacking state of the Robot
	 */
	public void setAttackingState(boolean state)
	{
		this.isAttacking = state;
	}

	/**
	 * Sets the battling state of the Robot using the given boolean
	 * @param state Set the battling state of the Robot
	 */
	public void setBattleState(boolean state)
	{
		this.isBattling = state;
	}

	/**
	 * Sets the drawMenu flag to true for paintComponent to draw the in-game
	 * menu and start listening for mouseEvents
	 */
	public void drawMenu()
	{
		System.out.println("Entered drawMenu() in GamePanel");
		this.drawMenu = true;
		this.addMouseListener(this);
		this.paused = true;
		this.repaint();
	}

	/**
	 * Sets the drawMenu flag to false for paintComponent to stop drawing the
	 * in-game menu and stop listening for mouseEvents
	 */
	public void cancelMenu()
	{
		this.drawMenu = false;
		this.removeMouseListener(this);
		this.paused = false;
		this.statusBar.menuClosed();
		this.repaint();
	}

	/**
	 * Close the JFrame
	 */
	public void close()
	{
		this.game.setVisible(false);
		this.game.close();
	}

	/**
	 * Set the show help menu flag to true and repaint() the JPanel
	 */
	public void showHelpMenu()
	{
		this.showHelp = true;
		this.repaint();
	}

	/**
	 * Receives KeyEvents from the JFrame; Changes the keysPressed array to
	 * false for use by the move() method
	 * @Override The KeyListener keyReleased() method
	 */
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W)
			keysPressed[0] = false;
		else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D)
			keysPressed[1] = false;
		else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S)
			keysPressed[2] = false;
		else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
			keysPressed[3] = false;
		else if (key == KeyEvent.VK_ESCAPE)
			this.statusBar.openMenu();

	}

	/**
	 * Receives mouseEvents from JFrame when the in-game menu is open
	 * @Override The MouseListener mouseClicked() method
	 */
	public void mouseClicked(MouseEvent e)
	{
		// Exits the help menu when mouse is clicked
		if (this.showHelp)
		{
			this.showHelp = false;
			this.repaint();
			return;
		}

		// Checks for the correct button based on the x and y location
		int x = e.getX();
		int y = e.getY();
		for (int rectangle = 0; rectangle < this.rectangles.length; rectangle++)
		{
			if (this.rectangles[rectangle].isWithin(x, y))
				this.rectangles[rectangle].clicked();
		}
	}

	/**
	 * Called by the in-game Timer
	 * @Override The ActionListener actionPerformed() method
	 */
	public void actionPerformed(ActionEvent e)
	{
		this.process();
	}

	// Filler methods for KeyListener
	@Override
	public void keyTyped(KeyEvent e)
	{
		return;
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
