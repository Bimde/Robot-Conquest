package game;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * The Main JFrame of Robot Conquest (contains main method) Sets up JFrame,
 * switches between JPanels and contains major public variables
 * 
 * @author Bimesh De Silva
 * @version January 2014
 *
 */
public class Game extends JFrame implements ActionListener
{
	private Square[][] grid;
	private LevelSelector levelSelector;
	private final String RETURN_TO_MENU = "Return to Main Menu";
	private Menu menu;
	private Robot robot;
	private int width, height;
	private GamePanel gamePanel;
	private StatusBar statusBar;
	private GameOver gameOver;
	private int noOfEnemies;
	private ArrayList<Enemy> enemies;
	private int enemyTotalHealth;
	private String currentPlayer = "Player 1";
	private String[] allPlayers;
	public static Image icon;
	private int noOfKeys;
	public static final String NEW_PLAYER = "Create new player...";
	public static final int MAX_NAME_LENGTH = 8;

	// Ma in variables
	public static final int NO_OF_ROWS = 24;
	// public final int PIXEL_SHRINK = 1;
	public static final int SQUARE_WIDTH = 30;

	// General objects
	public final char OPEN = 'a';
	public final char WALL = 'b';
	public final char START = 'c';
	public final char END = 'd'; // Becomes a 'e' when passable
	public final char NORTH_ONLY = 'f';
	public final char EAST_ONLY = 'g';
	public final char SOUTH_ONLY = 'h';
	public final char WEST_ONLY = 'i';

	// Items that could be picked up
	public final char REVEALED_IMAGE = 'j'; // All items are hidden 'a'
											// until revealed
	public final char KEY = 'z';
	public final char HEALTH_BOOST = 'x';
	public final char DAMAGE_BOOST = 'w';

	// Enemies *** Becomes an 'a' (an open square) when defeated
	public final char BOSS = 'k';
	public final char ENEMY = 'l';
	public final char HIDDEN_ENEMY = 'v'; // Becomes a normal enemy when
											// detected

	// Data File variables
	private int robotHealth;
	private int robotDamage;

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args)
	{
		new Game();
	}

	/**
	 * Main JFrame constructor, never closes until user ends game
	 */
	public Game()
	{
		super("Robot Conquest");
		try
		{
			this.loadData();
		}
		catch (IOException e)
		{
			System.err.println("trying loadData() in Game");
			System.exit(-1);
		}
		// this.setSize((this.resolution.height+((this.resolution.width-this.resolution.height)
		// / 3)), this.resolution.height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		// Declaring main variables
		this.setSize(this.width, this.height);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.newMenu();
		this.setVisible(true);

	}

	/**
	 * Creates a new Menu object
	 */
	public void newMenu()
	{
		this.menu = new Menu(this);
		this.add(menu);
	}

	/**
	 * Loads required modifiable information such as robot health, damage,
	 * created users and last played user
	 * @throws IOExceptiondddd
	 */
	private void loadData() throws IOException
	{
		// Load the game Icon
		File file = new File("Image10.png");
		icon = ImageIO.read(file);
		this.setIconImage(icon);

		// Load game data
		file = new File("Data.rbcq");
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			System.err
					.println("Trying to read file data.txt in loadData() method in Game");
			System.exit(-1);
		}

		// Load players
		this.robotHealth = Integer.parseInt(in.readLine());
		this.robotDamage = Integer.parseInt(in.readLine());
		int players = Integer.parseInt(in.readLine());
		this.allPlayers = new String[players + 1];
		for (int player = 0; player < allPlayers.length - 1; player++)
		{
			this.allPlayers[player] = in.readLine();
		}
		this.allPlayers[this.allPlayers.length - 1] = NEW_PLAYER;
		in.close();

		// Set frame values
		this.height = (NO_OF_ROWS * SQUARE_WIDTH);
		this.width = (this.height / 3) + this.height + 5;
		this.height += SQUARE_WIDTH - 1;
	}

	/**
	 * Getter for players String array
	 * @return An array of all the potential players
	 */
	public String[] getPlayers()
	{
		return this.allPlayers;
	}

	/**
	 * Creates a new LevelSelector object and adds it to the frame
	 */
	public void newLevelSelector()
	{
		// Creates a new LevelSelector
		this.levelSelector = null;
		this.levelSelector = new LevelSelector(this);
		this.setVisible(false);

		// Centers the frame
		this.setLocationRelativeTo(null);
		this.levelSelector.added();

		// Add the LevelSelector (JPanel) to the frame
		this.add(this.levelSelector);
		this.setVisible(true);
	}

	/**
	 * Creates new GamePanel and StatusBar objects and sets up required values
	 * @param level The level selected by the user
	 */
	public void newLevel(int level)
	{
		// Create the grid (and in turn the main variables, i.e. Enemies)
		this.setVisible(false);
		this.noOfKeys = 0;
		this.enemies = new ArrayList<Enemy>();
		this.gamePanel = new GamePanel(this);
		try
		{
			boolean goodFile = this.loadGrid("Level" + level + ".rbcq");
			if (!goodFile)
			{
				System.err.println("File is corrupted!");
				System.exit(0);
			}
		}
		catch (FileNotFoundException e)
		{
			System.err
					.println("catch FileNotFound Exception in newLevel() method in Game loading level file #"
							+ level);
		}

		// Initialize the objects
		this.statusBar = new StatusBar(this, gamePanel);
		this.gamePanel.addGameData(robot, statusBar, level);
		this.statusBar.loadData();

		// Creates a border layout to place the GamePanel and StatusBar JPanel
		this.setLayout(new BorderLayout());
		this.add(gamePanel, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.WEST);

		// Sets the size and other variables appropriate to the
		// current JPanel sizes
		this.setSize(this.width, this.height);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		// Give focus to the GamePanel to allow for listening events
		this.gamePanel.requestFocusInWindow();
		this.gamePanel.startGame();
	}

	/**
	 * Throwing FileNotFoundException because try/catch causes
	 * "in (BufferedReader) not initialized" errors
	 * @param fileName The File to load the Square array from
	 * @return If a valid grid was loaded
	 * @throws FileNotFoundException If the given fileName didn't reference a
	 *             proper file
	 */
	private boolean loadGrid(String fileName) throws FileNotFoundException
	{
		File file = new File(fileName);
		Scanner in = new Scanner(file);
		String nextLine;
		this.grid = new Square[NO_OF_ROWS][NO_OF_ROWS];
		boolean[] requiredElements = new boolean[2];

		// Loads the array of Square objects and checks for required elements
		for (int row = 0; row < NO_OF_ROWS; row++)
		{
			nextLine = in.nextLine().toLowerCase();
			for (int pos = 0; pos < NO_OF_ROWS; pos++)
			{
				try
				{
					char test = nextLine.charAt(pos);
					this.grid[row][pos] = new Square(test, this, gamePanel,
							row, pos);
				}
				catch (IndexOutOfBoundsException e)
				{
					System.err.println(e);
					System.err.println("File Corupted, line " + (row + 1)
							+ " doesn't have " + NO_OF_ROWS
							+ " elements in it!");
					in.close();
					return false;
				}
				if (!requiredElements[0]
						&& this.grid[row][pos].isStartHere())
				{
					this.loadRobot(row, pos);
					requiredElements[0] = true;
				}
				else if (!requiredElements[1]
						&& this.grid[row][pos].isExitHere())
				{
					requiredElements[1] = true;
					this.gamePanel.setExitLocation(row, pos);
				}
			}
		}
		in.close();

		// Check for validity
		for (int element = 0; element < requiredElements.length; element++)
		{
			if (!requiredElements[element])
			{
				System.err.println("Doesn't have all required elements");
				return false;
			}
		}
		return true;
	}

	/**
	 * Loads the Robot object at the start (given) position
	 * @param row The row the Robot is to start at
	 * @param column The column the Robot is to start at
	 */
	public void loadRobot(int row, int column)
	{
		this.robot = new Robot(row, column, this.robotHealth, this.robotDamage,
				this, this.gamePanel);
	}

	/**
	 * Getter for the currentPlayer String
	 * @return the current player
	 */
	public String getCurrentPlayer()
	{
		return this.currentPlayer;
	}

	/**
	 * Changes the current player to a new user-created player
	 * @param newName The name to change the current player to
	 */
	public void changeCurrentPlayer(String newName)
	{
		if (newName.charAt(0) != '*')
			this.currentPlayer = newName;
		else
			this.currentPlayer = newName.substring(1);
	}

	/**
	 * Receives ActionEvents
	 * @Override The abstract actionPerformed() method in ActionListener
	 */
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		if (command.equals(this.RETURN_TO_MENU))
		{
			if (!this.menu.isEnabled())
			{
				if (this.gamePanel.isEnabled())
				{
					this.gamePanel.setEnabled(false);
					this.remove(this.gamePanel);
				}
				else if (this.levelSelector.isEnabled())
				{
					this.levelSelector.setEnabled(false);
					this.remove(this.levelSelector);
				}
			}
		}
	}

	/**
	 * Creates a new GameOver object; called by GamePanel when a level is either
	 * lost or won
	 * @param state The state the battle ended in
	 */
	synchronized void gameOver(int state)
	{
		int timeLeft = statusBar.getTimeLeft();
		System.out.println("State: " + state);
		this.setVisible(false);
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			System.err.println("tried Thread.sleep for 1000 ms in gameOver");
		}
		this.refresh();
		this.gameOver = new GameOver(this.gamePanel, state, timeLeft, this);
		// this.add(this.gameOver, BorderLayout.CENTER);
		this.add(this.gameOver);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * Refreshes important game variables;
	 */
	synchronized void refresh()
	{
		this.remove(gamePanel);
		this.remove(statusBar);
		this.statusBar.setVisible(false);

		// Sets these variables to null so garbage collector can clear memory
		this.statusBar = null;
		this.enemies = null;
		this.robot = null;
		this.grid = null;
	}

	/**
	 * Getter for height
	 * @return the current height of the JFrame
	 */
	public int getHeight()
	{
		return this.height;
	}

	/**
	 * Getter for width
	 * @return the current width of the JFrame
	 */
	public int getWidth()
	{
		return this.width;
	}

	/**
	 * Getter for the grid of Square objects
	 * @return the current grid
	 */
	public Square[][] getGrid()
	{
		return this.grid;
	}

	/**
	 * Adds the given Enemy to the arrayList of Enemy objects
	 * @param enemy The Enemy object to add the the ArrayList
	 */
	public void addEnemy(Enemy enemy)
	{
		this.enemies.add(enemy);
		this.enemyTotalHealth += enemy.getHealth();
		this.noOfEnemies++;
	}

	/**
	 * Getter for the total health pool of Enemy Objects
	 * @return The total health pool of the Enemy objects
	 */
	public int getEnemyTotalHealth()
	{
		return this.enemyTotalHealth;
	}

	/**
	 * Adds a key to noOfKeys
	 */
	public void addKey()
	{
		this.noOfKeys++;
	}

	/**
	 * Getter for the required number of keys
	 * @return The current number of keys
	 */
	public int getNoOfKeys()
	{
		return this.noOfKeys;
	}

	/**
	 * Getter for the total number of enemies
	 * @return The number of enemies
	 */
	public int getNoOfEnemies()
	{
		return this.noOfEnemies;
	}

	/**
	 * Exits the program
	 */
	public void close()
	{
		System.exit(0);
	}

	/**
	 * Sets the given level's completed status to true through LevelSelector
	 * @param level
	 */
	public void levelCompleted(int level)
	{
		if (level != 10)
			this.levelSelector.levelCompleted(level / 2, level % 2);
	}
}
