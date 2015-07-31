package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JPanel;

/**
 * Allows the user to select levels with a GUI
 * 
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class LevelSelector extends JPanel implements MouseListener
{
	private Game game;
	private String playerName;
	private int height, width;
	private int[] rowPositions, columnPositions;
	private boolean[][] levelsCompleted;
	public static final int NO_OF_STAGES = 5;
	public static final int LEVELS_PER_STAGE = 2;
	private static final int MARGIN = 100;
	private static final int SPACE = 50;
	private static final int BUTTON_HEIGHT = 70;
	private static final int LEVEL_WIDTH = 200;
	private MenuItem[] stages;
	private MenuItem[][] levels;
	private int pressed;

	/**
	 * LevelSelector constructor
	 * @param game The JFrame
	 */
	public LevelSelector(Game game)
	{
		super();
		this.game = game;
	}

	/**
	 * Called by Game when the LevelSelector is being shown on the JFrame
	 */
	public void added()
	{
		this.height = Game.SQUARE_WIDTH * Game.NO_OF_ROWS;
		this.width = this.height + (this.height / 3);
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.setMinimumSize(new Dimension(this.width, this.height));
		this.playerName = game.getCurrentPlayer();
		this.pressed = -1;
		this.addMouseListener(this);

		// Create row and column positions for the Menu buttons
		this.rowPositions = new int[] { SPACE, BUTTON_HEIGHT, SPACE,
				BUTTON_HEIGHT, SPACE, BUTTON_HEIGHT, SPACE, BUTTON_HEIGHT,
				SPACE, BUTTON_HEIGHT };
		for (int row = 1; row < this.rowPositions.length; row++)
		{
			this.rowPositions[row] += this.rowPositions[row - 1];
		}

		this.columnPositions = new int[] { MARGIN - 25, BUTTON_HEIGHT,
				LEVEL_WIDTH,
				BUTTON_HEIGHT, LEVEL_WIDTH, BUTTON_HEIGHT };
		for (int column = 1; column < this.columnPositions.length; column++)
		{
			this.columnPositions[column] += this.columnPositions[column - 1];
		}

		// Load the level's completed
		try
		{
			this.loadData();
		}
		catch (IOException e)
		{
			System.err
					.println("Trying loadData() in LevelSelector with file: GameSave.rbcq");
		}

		// Create the menu buttons
		this.loadRectangles();
	}

	/**
	 * Called when a level is won to save progress
	 * @param stage The stage of the level
	 * @param level The level in the given stage (either 0 or 1)
	 */
	public void levelCompleted(int stage, int level)
	{
		this.levelsCompleted[stage][level] = true;
		try
		{
			this.writeLevelsToFile();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File wasn't able to be created!");
		}
	}

	/**
	 * Writes the current levels open to a data file
	 * @throws FileNotFoundException Through the PrintWriter
	 */
	private void writeLevelsToFile() throws FileNotFoundException
	{
		System.out.println("Entered write to file!");
		File file = new File("GameSave.rbcq");

		PrintWriter printWriter = new PrintWriter(file);
		for (int stage = 0; stage < NO_OF_STAGES; stage++)
		{
			for (int level = 0; level < LEVELS_PER_STAGE; level++)
			{
				if (this.levelsCompleted[stage][level] == true)
					printWriter.print("1");
				else
					printWriter.print("0");
			}
			printWriter.println();
		}
		printWriter.close();
	}

	/**
	 * Draws the Level selector buttons on the JPanel
	 * @Override The JPanel paintComponent() method
	 */
	public synchronized void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Draw stage rectangles
		for (int stage = 0; stage < this.stages.length; stage++)
		{
			this.stages[stage].draw(g);
			if (this.stages[stage].isPressed())
				this.pressed = stage;
		}

		// Draw current stage's levels
		if (this.pressed != -1)
		{
			this.levels[this.pressed][0].draw(g);
			this.levels[this.pressed][1].draw(g);
		}

	}

	/**
	 * Load's a previous game save file (if there is one)
	 * @return Whether or not a file was found
	 * @throws IOException If the file is corrupt and doesnt contain required
	 *             elements
	 */
	private synchronized boolean loadData() throws IOException
	{
		File file = new File("GameSave.rbcq");
		this.levelsCompleted = new boolean[NO_OF_STAGES][LEVELS_PER_STAGE];
		if (file.exists() && file.isFile())
		{
			BufferedReader in = new BufferedReader(new FileReader(file));

			// Loads the levels completed from the data file
			for (int stage = 0; stage < this.levelsCompleted.length; stage++)
			{
				String line = in.readLine();
				for (int level = 0; level < this.levelsCompleted[stage].length; level++)
				{
					if (Integer.parseInt(line.substring(level, level + 1)) == 1)
						this.levelsCompleted[stage][level] = true;
					// Didn't work without this line
					else
						this.levelsCompleted[stage][level] = false;
				}
			}
			in.close();
		}
		// If there isn;t a file, create a blank array
		else
		{
			System.out.println("Creating boolean array");
			for (int stage = 0; stage < levelsCompleted.length; stage++)
				for (int level = 0; level < levelsCompleted[stage].length; level++)
					this.levelsCompleted[stage][level] = false;
		}

		// Prevent a corrupt file from preventing user from playing any level
		this.levelsCompleted[0][0] = true;
		return true;
	}

	/**
	 * Create the Level selector's satge and level buttons using the
	 * pre-determined x and y locations
	 */
	private void loadRectangles()
	{
		// Create the stage buttons
		this.stages = new MenuItem[NO_OF_STAGES];
		for (int stage = 0; stage < this.stages.length; stage++)
		{
			Location location = new Location(0, 0,
					this.rowPositions[(stage * 2) + 1], this.columnPositions[1]);
			this.stages[stage] = new MenuItem(this,
					String.valueOf(stage + 1), location, BUTTON_HEIGHT,
					BUTTON_HEIGHT, stage, true);
		}

		// Create the level buttons
		this.levels = new MenuItem[NO_OF_STAGES][LEVELS_PER_STAGE];
		for (int stage = 0; stage < levelsCompleted.length; stage++)
			for (int level = 0; level < levelsCompleted[stage].length; level++)
				System.out.println("Stage: " + stage + "\n     Level: "
						+ level + "\n          State: "
						+ this.levelsCompleted[stage][level]);
		for (int stage = 0; stage < this.levels.length; stage++)
		{
			for (int level = 0; level < this.levels[stage].length; level++)
			{
				Location location = new Location(0, 0, this.rowPositions[5],
						this.columnPositions[(2 + (level * 2))]);
				this.levels[stage][level] = new MenuItem(this,
						stage, level, location,
						LEVEL_WIDTH, BUTTON_HEIGHT,
						this.levelsCompleted[stage][level]);
			}
		}
	}

	/**
	 * Creates a new Game
	 * @param tier
	 */
	public void newGame(int tier)
	{
		System.out.println("Entered new Level!");
		this.setEnabled(false);
		game.newLevel(tier);
		game.remove(this);
	}

	/**
	 * Calls the respective button that was clicked
	 * @Override Built in MouseListener method
	 */
	public synchronized void mouseClicked(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		boolean finding = true;
		for (int stage = 0; finding && stage < stages.length; stage++)
		{
			if (this.stages[stage].isWithin(x, y))
			{
				if (this.pressed != -1)
				{
					this.stages[this.pressed].closed();
				}
				this.stages[stage].clicked();
				finding = false;
			}
		}

		// Checks for the stage clicked to decide the level to open
		if (this.pressed != -1)
		{
			System.out.println("Pressed: " + this.pressed);
			for (int level = 0; finding && level < levels[pressed].length; level++)
			{
				if (this.levelsCompleted[this.pressed][level])
				{
					if (this.levels[this.pressed][level].isWithin(x, y))
					{
						this.levels[this.pressed][level].clicked();
						finding = false;
					}
				}
			}
		}

		// Repaint to update user selection
		this.repaint();
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
