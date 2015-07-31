package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * GameOver class extends JPanel implements MouseListener Displays the results
 * of a finished battle
 * 
 * @author Bimesh De Silva
 * @version January 2015
 */
public class GameOver extends JPanel implements MouseListener
{
	private int state;
	private Game game;
	private GamePanel gamePanel;
	private Color color;
	private int width, height;
	private MenuItem[] rectangles;
	private int[] columnPositions;
	private int[] rowPositions;
	private BufferedImage image;
	private String timeLeft;
	private static final int MARGIN = 100;
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 50;
	private static final int SPACE = 50;
	public static final String[] titles = { "     Retry", "Main Menu",
			"  Continue" };

	/**
	 * Constructs GameOver object using the given parameters (either "Defeat" or
	 * "Victory")
	 * 
	 * @param gamePanel The gamePanel which the battle took place in
	 * @param state The final state of the battle
	 * @param timeLeft The amount of time left (in seconds)
	 * @param game The JFrame which contains this JPanel
	 */
	public GameOver(GamePanel gamePanel, int state, int timeLeft, Game game)
	{
		super();
		System.out.println("State: "+state);

		// Set up the JPanel
		this.game = game;
		this.gamePanel = gamePanel;
		this.height = Game.NO_OF_ROWS * Game.SQUARE_WIDTH;
		this.width = height + (height / 3);
		this.setPreferredSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(width, height));

		// Create Rectangles and set them up as buttons (MenuItem)
		this.loadRectangles();
		this.calculateTime(timeLeft);
		this.state = state;
		try
		{
			this.loadImages();
		}
		catch (IOException e)
		{
			System.err.println("Trying loadImages() in GameOver");
		}

		// Optimize for MouseListener and display the components
		this.setFocusable(true);
		this.addMouseListener(this);
		this.setVisible(true);
		this.requestFocus();
		this.repaint();
		this.saveData();
	}

	/**
	 * Saves the users progress if the user passed the level
	 */
	private void saveData()
	{
		if (this.state > 0)
		{
			this.game.levelCompleted(this.gamePanel.getLevel());
		}
	}

	/**
	 * Converts time in seconds to minutes and seconds
	 * @param time in seconds
	 */
	public void calculateTime(int time)
	{
		int minutes = time / 60;
		int seconds = time % 60;
		this.timeLeft = String.format("Remaining Time:  %2d:%02d", minutes,
				seconds);
	}

	/**
	 * Changes the given Graphics object
	 * 
	 * @param g The Graphics object to change
	 */
	@Override
	public synchronized void paintComponent(Graphics g)
	{
		g.setColor(Color.GRAY);

		// Draw Rectangles (used as buttons)
		g.fillRect(0, 0, this.width, this.width);
		for (int item = 0; item < rectangles.length; item++)
		{
			this.rectangles[item].draw(g);
		}

		// Draw banner
		g.drawImage(image, this.columnPositions[0], this.rowPositions[0],
				((BUTTON_WIDTH * 3) + MARGIN + MARGIN),
				(this.rowPositions[1] - (MARGIN / 2)), game);

		// Draw text
		g.setColor(Color.WHITE);
		g.setFont(new Font("Calibri", Font.BOLD, 48));
		g.drawString(this.timeLeft, this.columnPositions[1],
				this.rowPositions[3]);
	}

	/**
	 * Loads the correct image for the JPanel based on the state
	 * @throws IOException is the file cannot be found
	 */
	private void loadImages() throws IOException
	{
		File file;
		if (this.state > 0)
			file = new File("Victory.png");
		else
			file = new File("Defeat.png");
		this.image = ImageIO.read(file);
	}

	/**
	 * Loads the MenuItem Objects required to draw the JPanel
	 */
	private synchronized void loadRectangles()
	{
		// Set up the x and y coordinates of each MenuItem
		this.columnPositions = new int[] { MARGIN - 20, BUTTON_WIDTH, MARGIN,
				BUTTON_WIDTH, MARGIN, BUTTON_WIDTH };
		for (int column = 1; column < this.columnPositions.length; column++)
		{
			this.columnPositions[column] += this.columnPositions[column - 1];
		}
		this.rowPositions = new int[] { MARGIN, BUTTON_HEIGHT * 4, SPACE,
				BUTTON_HEIGHT * 2, SPACE, BUTTON_HEIGHT };
		for (int row = 1; row < this.rowPositions.length; row++)
		{
			this.rowPositions[row] += this.rowPositions[row - 1];
		}

		// Create the MenuItems using the creates x and y positions
		this.rectangles = new MenuItem[titles.length];
		for (int rectangle = 0; rectangle < titles.length; rectangle++)
		{
			Location location = new Location(0, 0, this.rowPositions[5],
					this.columnPositions[rectangle * 2]);
			this.rectangles[rectangle] = new MenuItem(this,
					titles[rectangle], location, BUTTON_WIDTH,
					BUTTON_HEIGHT, rectangle);
		}
	}

	/**
	 * Calls the JFrame to re-construct the just completed level
	 */
	public void retry()
	{
		this.setVisible(false);
		this.setEnabled(false);
		this.game.remove(this);
		this.game.newLevel(this.gamePanel.getLevel());
	}

	/**
	 * Calls the JFrame to re-construct the main menu
	 */
	public void returnToMainMenu()
	{
		this.setVisible(false);
		this.setEnabled(false);
		this.game.remove(this);
		this.game.newMenu();
	}

	/**
	 * Calls the JFrame to re-construct the level selector
	 */
	public void toLevelSelector()
	{
		this.setVisible(false);
		this.setEnabled(false);
		this.game.remove(this);
		this.game.newLevelSelector();
	}

	/**
	 * Called when the user clicks their mouse; Clicks the button within the
	 * region of the mouse click
	 * @Override
	 */
	public void mouseClicked(MouseEvent e)
	{
		System.out.println("Entered MouseClicked()");
		int x = e.getX();
		int y = e.getY();

		for (int rectangle = 0; rectangle < titles.length; rectangle++)
		{
			if (this.rectangles[rectangle].isWithin(x, y))
			{
				this.rectangles[rectangle].clicked();
			}
		}
	}

	// Unused MouseListener methods
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
