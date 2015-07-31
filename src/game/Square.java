package game;

/**
 * Keeps track of each square in the grid including the pictures to be shown and
 * the objects contained within the square
 * 
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class Square
{
	// Create variables to keep track of
	public int itemImgRef;
	private char overlay;
	private GamePanel gamePanel;
	private Location location;
	private boolean isEnterable;
	private boolean isDirectionHere;
	private boolean isRockHere;
	private boolean isItemHere;
	private boolean isRobotHere;
	private boolean isEnemyHere;
	private boolean isStartHere;
	private boolean isExitHere;
	private Direction direction;
	private Enemy enemy;
	private Game game;
	private Item item;

	/**
	 * Constructor for each Square of the grid
	 * @param object	The constant representation (Game class)
	 * @param game		The JFrame
	 * @param gamePanel	The JPanel
	 * @param row		The row position on the grid
	 * @param column	The column position on the grid
	 */
	public Square(char object, Game game, GamePanel gamePanel, int row, int column)
	{
		this.game = game;
		this.gamePanel = gamePanel;
		this.location = new Location(row, column);
		this.overlay = object;
		System.out.println("\nOverlay: " + this.overlay);
		this.itemImgRef = (int) (object - game.OPEN);
		System.out.println("ImgItemRef: " + this.itemImgRef);
		this.adjust();
		System.out.println("After adjust; ImageItemRef: " + this.itemImgRef);
	}
	
	/**
	 * Returns the overlay of the Square
	 * @return
	 */
	public char getOverlay()
	{
		return this.overlay;
	}

	/**
	 * Sets variables to current values based on pre-set variables
	 */
	private void adjust()
	{
		// Open
		if (this.overlay == game.OPEN)
		{
			this.isEnterable = true;
		}
		// Direction Marker
		else if (this.overlay >= game.NORTH_ONLY
				&& this.overlay <= game.WEST_ONLY)
		{
			this.isDirectionHere = true;
			this.isEnterable = true;
			if (this.overlay == game.NORTH_ONLY)
				this.direction = Direction.NORTH;
			else if (this.overlay == game.EAST_ONLY)
				this.direction = Direction.EAST;
			else if (this.overlay == game.SOUTH_ONLY)
				this.direction = Direction.SOUTH;
			else
				this.direction = Direction.WEST;
		}
		//Wall
		else if (this.overlay == game.WALL)
		{
			this.isRockHere = true;
		}
		//Start
		else if (this.overlay == game.START)
		{
			this.isRobotHere = true;
			this.isStartHere = true;
			this.isEnterable = true;
		}
		//Exit
		else if (this.overlay == game.END)
		{
			this.isExitHere = true;
		}
		//Item
		else if (this.overlay >= game.DAMAGE_BOOST && this.overlay <= game.KEY)
		{
			this.itemImgRef = 0;
			this.isItemHere = true;
			this.item = new Item(this.overlay);
			
			if (this.overlay == game.KEY)
				this.game.addKey();
		}
		//Enemy
		else if (this.overlay == game.BOSS || this.overlay == game.HIDDEN_ENEMY
				|| this.overlay == game.ENEMY)
		{
			if (this.overlay == game.HIDDEN_ENEMY)
			{
				this.itemImgRef = 0;
			}
			this.enemy = new Enemy(this.overlay, this.game, this.location, gamePanel, this);
			this.isEnemyHere = true;
			game.addEnemy(this.enemy);
		}
	}

	/**
	 *Unlock the exit at the current Square
	 */
	public void exitUnlocked()
	{
		if (this.overlay == game.END)
		{
			this.itemImgRef++;
			this.isEnterable = true;
		}
	}

	/**
	 * Set an Image reference for the current item
	 */
	public void itemDetected()
	{
		this.itemImgRef = (int) (game.REVEALED_IMAGE - game.OPEN);
		this.isEnterable = true;
	}

	/**
	 * PRECONDITION: used isItemHere() and returned true
	 */
	public Item itemPickedUp()
	{
		this.isItemHere = false;
		this.overlay = game.OPEN;
		this.itemImgRef = 0;
		return this.item;
	}

	/**
	 * Set an Image reference for the current Enemy
	 */
	public void enemyDetected()
	{
		if (this.overlay == game.HIDDEN_ENEMY)
		{
			this.overlay = game.ENEMY;
			this.itemImgRef = (int) (this.overlay - game.OPEN);
			this.enemy.detected();
		}
	}

	/**
	 * Delete current Enemy
	 */
	public void enemyDefeated()
	{
		this.isEnterable = true;
		this.itemImgRef = 0;
		this.isEnemyHere = false;
		this.overlay = game.OPEN;
		this.enemy = null;
	}

	/**
	 * Getter for isDirectionHere
	 * @return If a Direction is here
	 */
	public boolean isDirectionHere()
	{
		return this.isDirectionHere;
	}

	/**
	 * Getter for direction
	 * @return If a Direction is here
	 */
	public Direction passableDirection()
	{
		return this.direction;
	}

	/**
	 * Getter for isEmpty
	 * @return If the Square is empty
	 */
	public boolean isEmpty()
	{
		return this.isEnterable;
	}

	/**
	 * Getter for isEnemyHere
	 * @return If an Enemy is here
	 */
	public boolean isEnemyHere()
	{
		return this.isEnemyHere;
	}

	/**
	 * Getter for isRobotHere
	 * @return If a Robot is here
	 */
	public boolean isRobotHere()
	{
		return this.isRobotHere;
	}

	/**
	 * Getter for isItemHere
	 * @return If an Item is here
	 */
	public boolean isItemHere()
	{
		return this.isItemHere;
	}

	/**
	 * Getter for isStartHere
	 * @return If a start marker is here
	 */
	public boolean isStartHere()
	{
		return this.isStartHere;
	}

	/**
	 * Getter for isExitHere
	 * @return If an exit marker is here
	 */
	public boolean isExitHere()
	{
		return this.isExitHere;
	}

	/**
	 * Getter for isRockHere
	 * @return If a rock is here
	 */
	public boolean isRockHere()
	{
		return this.isRockHere;
	}

	/**
	 * Getter for enemy
	 * @return The current Enemy
	 */
	public Enemy getEnemy()
	{
		return this.enemy;
	}
}
