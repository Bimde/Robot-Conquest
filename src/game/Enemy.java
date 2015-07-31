package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * The Enemy class An object that opposes the Robot object
 * 
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class Enemy implements ActionListener
{
	private boolean isHidden;
	private int health, damage, tier;
	private Location location;
	private int healthPercentage;
	Image image;
	Game game;
	GamePanel gamePanel;
	private final int[] healthTiers = { 500, 150, 150 };
	private final int[] damageTiers = { 100, 50, 50 };
	private boolean isDisabaled;
	private Robot robot;
	private Timer timer;
	private final int delay = 1000;
	private boolean isAttacked;
	private Square container;

	/**
	 * Creates an Enemy object based on the tier 0 being a boss (highest
	 * difficulty enemy) 1 being normal enemy 2 being a hidden enemy
	 * @param type The type of enemy
	 */
	public Enemy(char type, Game game, Location location, GamePanel gamePanel,
			Square container)
	{
		this.game = game;
		this.gamePanel = gamePanel;
		this.container = container;
		this.location = new Location(location.row, location.column,
				location.row * Game.SQUARE_WIDTH, location.column
						* Game.SQUARE_WIDTH);
		if (type == game.HIDDEN_ENEMY)
			this.tier = 2;
		else
			this.tier = (int) (type - game.BOSS);
		
		// Get the image based on the tier
		if (this.tier == 2 || this.tier == 1)
			this.image = this.gamePanel.getImage(11);
		else
			this.image = this.gamePanel.getImage(10);

		this.health = this.healthTiers[this.tier];
		this.damage = this.damageTiers[this.tier];

		// Hides if it is a hidden enemy
		if (this.tier == 2)
		{
			this.isHidden = true;
		}

		// Cretae a timer for attacking the Robot
		this.timer = new Timer(delay, this);
		this.isAttacked = false;
	}

	/**
	 * Draws the Enemy object is it isn't hidden
	 * @param g
	 */
	public void draw(Graphics g)
	{
		// Draw the Enemy object if it isn't hidden
		if (!this.isHidden)
		{
			g.drawImage(this.image, this.location.column * Game.SQUARE_WIDTH,
					this.location.row
							* Game.SQUARE_WIDTH,
					Game.SQUARE_WIDTH, Game.SQUARE_WIDTH, this.game);

			// Draw Health Bar
			this.healthPercentage = (this.health * 100)
					/ this.healthTiers[this.tier];
			if (this.healthPercentage == 0 && this.health != 0)
			{
				this.healthPercentage = 1;
			}

			// Draw a health bar
			g.setColor(Color.BLACK);
			g.drawRect(this.location.x - 6, this.location.y - 6,
					Game.SQUARE_WIDTH + 11, 11);
			g.setColor(Color.RED);
			g.fillRect(this.location.x - 5, this.location.y - 5,
					Game.SQUARE_WIDTH + 10, 10);
			g.setColor(Color.GREEN);
			g.fillRect(
					this.location.x - 5,
					this.location.y - 5,
					(int) ((this.healthPercentage / 100.0)
							* Game.SQUARE_WIDTH + 10),
					10);
		}
	}

	/**
	 * Starts the Enemy object's Timer object (to attack Robot)
	 * @param robot The robot to start attacking (using the Timer object)
	 */
	public void startBattle(Robot robot)
	{
		this.robot = robot;
		timer.start();
	}

	/**
	 * "Deletes" this object by stopping the Timer
	 */
	public void dispose()
	{
		if(this.timer.isRunning())
			this.timer.stop();
		this.container.enemyDefeated();
	}

	/**
	 * Reduces the current health by the given damage
	 * @param damage The amount of damage dealt to the Enemy by the Robot
	 */
	public void loseHealth(int damage)
	{
		// "Delete" this object if the health is below 0
		if (damage >= this.health)
		{
			this.health = 0;
			this.isDisabaled = true;
			this.gamePanel.setBattleState(false);
			this.dispose();
		}
		// If not, reduce the health by the given damage parameter
		else
		{
			this.health -= damage;
			this.robot.loseHealth(this.damage);
			this.isAttacked = true;
		}
		// Tell the timer to not attack the robot this cycle
		this.gamePanel.setAttackingState(false);
	}

	/**
	 * Getter for the health variables
	 * @return The current health of the Enemy object
	 */
	public int getHealth()
	{
		return this.health;
	}

	/**
	 * Getter for the damage variable
	 * @return The current health of the Enemy object
	 */
	public int getDamage()
	{
		return this.damage;
	}

	/**
	 * Getter for the tier variable
	 * @return The current tier of the Enemy object
	 */
	public int getTier()
	{
		return this.tier;
	}

	/**
	 * Getter for the isDisabled variable
	 * @return The disabled status of the Enemy object
	 */
	public boolean isDisabled()
	{
		return this.isDisabaled;
	}

	/**
	 * Called when a Robot object gets within 2 grid Changes a hidden enemy to a
	 * normal enemy
	 */
	public void detected()
	{
		if (this.isHidden)
		{
			this.tier--;
			this.isHidden = false;
		}
	}

	/**
	 * Getter for the isHidden variable
	 * @return The hidden status of the Enemy object
	 */
	public boolean isHidden()
	{
		return this.isHidden;
	}

	/**
	 * ActionListener method to be called by the Timer object
	 */
	public synchronized void actionPerformed(ActionEvent arg0)
	{
		if (!this.isDisabaled)
		{
			// Attacks the robot if the current cycle isn't set to be ignored by
			// the loseHealth() method
			System.out.println("Entered ActionPerformed in Enemy at: row: "
					+ this.location.row + "	Column: " + this.location.column);
			if (!this.isAttacked)
				this.robot.loseHealth(this.damage);
			else
				this.isAttacked = false;
		}
	}
}
