package game;

/**
 * Allows for easy storage of the Location variables (x, y, column, row)
 * 
 * @author Bimesh De Silva
 * @version January 2015
 *
 */
public class Location
{
	public int row;
	public int column;
	public int y;
	public int x;

	/**
	 * Use this constructor for creating a Location object with only a
	 * row/column
	 * 
	 * @param row The row in the 2-d array
	 * @param column The column in the 2-d array
	 */
	public Location(int row, int column)
	{
		this.row = row;
		this.column = column;
	}

	/**
	 * Use this constructor for creating a Location object with both a
	 * row/column and a x/y coordinate
	 * 
	 * @param row The row in the 2-d array
	 * @param column The column in the 2-d array
	 * @param y The y coordinate on the JPanel
	 * @param x The x coordinate of the JPanel
	 */
	public Location(int row, int column, int y, int x)
	{
		this.row = row;
		this.column = column;
		this.y = y;
		this.x = x;
	}

	/**
	 * Allows for the Location object to be changed without creating a new
	 * Location
	 * @param rowChange The change in row position
	 * @param columnChange The change in column position
	 */
	public void change(int rowChange, int columnChange)
	{
		this.column += columnChange;
		this.row += rowChange;
	}
}
