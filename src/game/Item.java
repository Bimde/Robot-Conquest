package game;

/**
 * Represents each individual item on the GamePanel grid
 * 
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class Item
{
	private char type;
	private int value;

	/**
	 * Constructor for Item;
	 * @param type The type of Item
	 */
	public Item(char type)
	{
		this.type = type;

		// Randomization gives the user a bit of suspense when getting an item
		this.value = (int) (Math.random() * 20) + 31;
	}

	/**
	 * Getter for the current type of Item
	 * @return The character representation of the item (equal to a public
	 *         static constant in Game)
	 */
	public char getType()
	{
		return this.type;
	}

	/**
	 * Getter for the value of the item
	 * @return The randomly determined value
	 */
	public int getValue()
	{
		return this.value;
	}
}
