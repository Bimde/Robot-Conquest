package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Main Menu for the Game; First JPanel loaded by JFrame
 * 
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class Menu extends JPanel implements MouseListener
{
	private int width, height;
	private String userName;
	private String[] userNames;
	private final String[] titles = { null,
			"Switch Player", "     Play", "How to Play ", "   Credits" };
	BufferedImage[] images;
	static final int BUTTON_HEIGHT = 70;
	private int buttonWidth;
	static final int MARGIN = 250;
	static final int RATIO = 7;
	private MenuItem[] rectangles;
	public static final Color BUTTON_COLOR = Color.BLUE;
	private static final int NO_OF_IMAGES = 3;
	private int[] rowPositions;
	private boolean drawInstructions;
	private Game game;

	/**
	 * Constructor for Menu object
	 * @param game The JFrame
	 */
	public Menu(Game game)
	{
		// Sets up JPanel
		super();
		this.game = game;

		// Sets up the JPanel
		this.height = Game.SQUARE_WIDTH * Game.NO_OF_ROWS;
		this.width = this.height + (this.height / 3);
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.setMinimumSize(new Dimension(this.width, this.height));

		// Finds player names
		this.userNames = game.getPlayers();
		this.drawInstructions = false;
		boolean finding = true;
		for (int name = 0; finding && name < this.userNames.length; name++)
		{
			if (this.userNames[name].charAt(0) == '*')
			{
				this.userName = this.userNames[name].substring(1);
				finding = false;
			}
		}
		this.titles[0] = "Current Player: " + userName;
		try
		{
			this.loadImages();
		}
		catch (IOException e)
		{
			System.err
					.println("File IO excpetion in loadImages() method from Menu constructor");
		}

		// pre-determining y positions for menu buttons
		this.rowPositions = new int[] { 100, BUTTON_HEIGHT, MARGIN / RATIO,
				BUTTON_HEIGHT, MARGIN / RATIO,
				BUTTON_HEIGHT, MARGIN / RATIO, BUTTON_HEIGHT, MARGIN / RATIO,
				BUTTON_HEIGHT, 50 };
		for (int row = 1; row < this.rowPositions.length; row++)
		{
			this.rowPositions[row] += this.rowPositions[row - 1];
		}
		this.buttonWidth = this.width - (MARGIN + MARGIN);
		this.loadRectangles();

		// Optimize for MouseListener
		this.addMouseListener(this);
		this.setFocusable(true);
		this.repaint();
		this.setVisible(true);
		this.requestFocus();
	}

	/**
	 * Load the Menu buttons
	 */
	private void loadRectangles()
	{
		this.rectangles = new MenuItem[this.titles.length];
		for (int rectangle = 0; rectangle < this.titles.length; rectangle++)
		{
			Location location = new Location(0, 0,
					this.rowPositions[(rectangle * 2) + 1] + 10, MARGIN);
			this.rectangles[rectangle] = new MenuItem(this,
					this.titles[rectangle], location, this.buttonWidth,
					BUTTON_HEIGHT, rectangle);
		}
	}

	/**
	 * Load required Images
	 * @throws IOException If Image isn't found
	 */
	private void loadImages() throws IOException
	{
		this.images = new BufferedImage[NO_OF_IMAGES];
		for (int image = 0; image < this.images.length; image++)
		{
			File file = new File("MenuImage" + image + ".png");
			this.images[image] = ImageIO.read(file);
		}
	}

	/**
	 * Draws Banner and background
	 * @Override JPanel paintComponent method
	 */
	public synchronized void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (!this.drawInstructions)
		{
			g.drawImage(this.images[0], 0, 0, this.width, this.height,
					this.game);
			// Draws the menu buttons
			for (int item = 0; item < rectangles.length; item++)
			{
				this.rectangles[item].draw(g);
			}

			// Draw Banner
			g.drawImage(this.images[1], 10, 10, this.width - 20,
					this.rowPositions[1] - 30, this.game);
		}
		else
			// Draw instructions
			g.drawImage(this.images[2], 0, 0, this.width, this.height,
					this.game);
	}

	/**
	 * Creates a LevelSelector object by calling the Game
	 */
	public void newGame()
	{
		this.setVisible(false);
		this.setEnabled(false);
		this.game.remove(this);
		this.game.newLevelSelector();
	}

	/**
	 * Getter for the current player name
	 * @return The current player name
	 */
	public String getPlayerName()
	{
		return this.userName;
	}

	/**
	 * Switch the current player
	 */
	public void switchPlayer()
	{
		ImageIcon icon = new ImageIcon(Game.icon);
		String newName = (String) JOptionPane.showInputDialog(this,
				"Select a player", "Switch Player", JOptionPane.PLAIN_MESSAGE,
				icon, this.userNames, this.userName);
		if (newName.equals(Game.NEW_PLAYER))
		{
			// Check for validity
			boolean valid = true;
			do
			{
				valid = true;
				newName = (String) JOptionPane
						.showInputDialog("Enter a new player name: ");
				if (newName.length() > Game.MAX_NAME_LENGTH)
				{
					valid = false;
					JOptionPane.showMessageDialog(this, "Name must be under "
							+ Game.MAX_NAME_LENGTH + " characters long!",
							"Name Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					for (int name = 0; valid
							&& name < this.userNames.length - 1; name++)
					{
						if (newName.equals(this.userNames[name]))
						{
							valid = false;
							JOptionPane
									.showMessageDialog(
											this,
											"Name must not be the name as a current player!",
											"Name Error",
											JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
			while (!valid);
			String[] temporary = userNames;
			userNames = new String[temporary.length + 1];
			userNames[0] = "*" + newName;
			for (int name = 1; name < temporary.length; name++)
				if (temporary[name - 1].charAt(0) == '*')
					userNames[name] = temporary[name - 1].substring(1);
				else
					userNames[name] = temporary[name - 1];
		}
		else
		{
			for (int name = 0; name < this.userNames.length; name++)
			{
				if (this.userNames[name].equals(newName))
				{
					if (newName.charAt(0) != '*')
						this.userNames[name] = "*" + this.userNames[name];
					else
						this.userNames[name] = newName;
				}
				else if (this.userNames[name].charAt(0) == '*')
					this.userNames[name] = this.userNames[name].substring(1);
			}
		}
		if (newName.charAt(0) != '*')
			newName = "*" + newName;
		this.switchPlayerName(newName);
	}

	/**
	 * Change the current player to a new one
	 * @param newName
	 */
	private void switchPlayerName(String newName)
	{
		this.game.changeCurrentPlayer(newName);
		this.userName = newName.substring(1);
		this.titles[0] = "Current Player: " + userName;
		this.rectangles[0].changeText(this.titles[0]);
		this.repaint();
	}

	/**
	 * Gets MouseEvents and calls respective button
	 * @Override MouseListener method
	 */
	public void mouseClicked(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();

		if (this.drawInstructions)
		{
			this.drawInstructions = false;
			this.repaint();
		}
		else
		{
			for (int rectangle = 0; rectangle < this.titles.length; rectangle++)
			{
				if (this.rectangles[rectangle].isWithin(x, y))
				{
					this.rectangles[rectangle].clicked();
				}
			}
		}
	}

	/**
	 * Sets flag to draw instructions
	 */
	public void instructions()
	{
		this.drawInstructions = true;
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

	/**
	 * Display credits
	 */
	public void credits()
	{
		JOptionPane
				.showMessageDialog(
						this,
						"Developed By: Bimesh De Silva for ICS3U6\nVersion: 1.0 January 2015");

	}
}
