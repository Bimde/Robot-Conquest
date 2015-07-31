package game;

/**
 * The Direction class 
 * Allows for easy storage / reference to direction of objects
 * 
 * @author Bimesh De Silva
 * @version January 2015
 */
public class Direction
{
	// Direction class constants (for use by other classes)
	public static final Direction NORTH = new Direction(0);
	public static final Direction EAST = new Direction(2);
	public static final Direction SOUTH = new Direction(4);
	public static final Direction WEST = new Direction(6);
	public static final Direction NORTH_EAST = new Direction(1);
	public static final Direction SOUTH_EAST = new Direction(3);
	public static final Direction SOUTH_WEST = new Direction(5);
	public static final Direction NORTH_WEST = new Direction(7);

	/**
	 * The String values of each of the directions
	 */
	private static String[] directions = { "North", "North-East", "East",
			"South-East", "South", "South-West", "West", "North-West" };

	private int reference;

	/*
	 * Private constructor to create the Direction constants
	 */
	private Direction(int reference)
	{
		this.reference = reference;
	}

	/**
	 * Getter for the number value of the Direction constant
	 * @return The number value of the current Direction constant
	 */
	public int number()
	{
		return this.reference;
	}

	/**
	 * Getter for the String value of the Direction constant
	 * @return The String value of the current Direction constant
	 */
	public String toString()
	{
		return directions[this.reference];
	}
}
