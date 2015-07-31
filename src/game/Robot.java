package game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * The Robot object representing the user
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class Robot
{
	private int health, damage;
	private Location location;
	private boolean isDisabaled;
	private Direction direction;
	private Game game;
	private final int NO_OF_IMAGES = 8;
	private BufferedImage[] images;
	public final int MAX_HEALTH = 1000;
	public final int MAX_DAMAGE = 120;
	private GamePanel gamePanel;

	/**
	 * Constructor for Robot
	 * @param row The row to place the Robot at
	 * @param column The column to place the Robot at
	 * @param health The starting health
	 * @param damage The starting damage
	 * @param game The JFrame
	 * @param gamePanel The JPanel
	 */
	public Robot(int row, int column, int health, int damage, Game game,
			GamePanel gamePanel)
	{
		this.gamePanel = gamePanel;
		this.location = new Location(row, column, (column * Game.SQUARE_WIDTH),
				(row * Game.SQUARE_WIDTH));
		this.direction = Direction.NORTH;
		this.health = health;
		this.damage = damage;
		this.game = game;
		this.loadImages();
	}

	/**
	 * Getter for Location
	 * @return the current Location
	 */
	public Location getLocation()
	{
		return this.location;
	}

	/**
	 * Change the Robot's Location
	 * @param rowChange The row change
	 * @param columnChange The column change
	 */
	public void move(int rowChange, int columnChange)
	{
		this.location.change(rowChange, columnChange);
	}

	/**
	 * Determines Direction based on movement
	 * @param rowChange The row change
	 * @param columnChange The column change
	 */
	public void changeDirection(int rowChange, int columnChange)
	{
		if (rowChange > 0 && columnChange > 0)
			this.direction = Direction.SOUTH_EAST;
		else if (rowChange > 0 && columnChange < 0)
			this.direction = Direction.SOUTH_WEST;
		else if (rowChange < 0 && columnChange < 0)
			this.direction = Direction.NORTH_WEST;
		else if (rowChange < 0 && columnChange > 0)
			this.direction = Direction.NORTH_EAST;
		else if (rowChange < 0)
			this.direction = Direction.NORTH;
		else if (rowChange > 0)
			this.direction = Direction.SOUTH;
		else if (columnChange > 0)
			this.direction = Direction.EAST;
		else if (columnChange < 0)
			this.direction = Direction.WEST;
	}

	/**
	 * Loads required images
	 */
	private void loadImages()
	{
		this.images = new BufferedImage[this.NO_OF_IMAGES];
		for (int image = 0; image < this.NO_OF_IMAGES; image++)
		{
			try
			{
				File file = new File("Robot" + image + ".png");
				this.images[image] = ImageIO.read(file);
				System.out.println("Loaded Image: Robot" + image);
			}
			catch (IOException e)
			{
				System.err.println("loading ROBOT image #" + image
						+ " - loadImages() in Main class");
			}
		}
	}

	/**
	 * Draws itself on the JPanel
	 * @param g The Graphics object
	 */
	public void draw(Graphics g)
	{
		// Determine the Image based on the Direction's number reference
		Image image = this.images[(this.direction.number())];
		if (image == null)
			System.err
					.println("Robot Image is null; Image Reference: "
							+ this.direction.number());
		g.drawImage(image, this.location.column * Game.SQUARE_WIDTH,
				this.location.row
						* Game.SQUARE_WIDTH,
				Game.SQUARE_WIDTH, Game.SQUARE_WIDTH, this.game);
	}

	/**
	 * Getter for the Direction
	 * @return The current Direction
	 */
	public Direction getDirection()
	{
		return this.direction;
	}

	/**
	 * Reduces the current health variable
	 * @param damage The amount to reduce the health by
	 */
	public void loseHealth(int damage)
	{
		if (this.health != 0)
		{
			// Destroys itself if the health is 0 or less
			if (damage >= this.health)
			{
				this.health = 0;
				this.isDisabaled = true;
				this.gamePanel.setState(-1);
				this.gamePanel.endGame();
			}
			else
				this.health -= damage;
		}
	}

	/**
	 * Getter for disabled
	 * @return Whether of not the Robot is disabled
	 */
	public boolean isDisabled()
	{
		return this.isDisabaled;
	}

	/**
	 * Getter for health
	 * @return Current health of the Robot
	 */
	public int getHealth()
	{
		return this.health;
	}

	/**
	 * Getter for damage
	 * @return Current damage of the Robot
	 */
	public int getDamage()
	{
		return this.damage;
	}

	/**
	 * Increases the health variable
	 * @param health
	 */
	public void healthBoost(int health)
	{
		this.health += health;
		if (this.health > this.MAX_HEALTH)
			this.health = this.MAX_HEALTH;
	}

	/**
	 * Increases the damage variable
	 * @param damage
	 */
	public void damageBoost(int damage)
	{
		this.damage += damage;
		if (this.damage > this.MAX_DAMAGE)
			this.damage = this.MAX_DAMAGE;
	}
}
