package data;

/** 이동경로를 나타내는 클래스 */
public class Movement
{
	private double x, y;        // 좌표
	private String description; // 설명
	private String direction;   // 방향

	/**
	 * 생성자
	 * @param x
	 * @param y
	 * @param description
	 * @param direction
	 */
	public Movement(double x, double y, String description, String direction)
	{
		this.x = x;
		this.y = y;
		this.description = description;
		this.direction = direction;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(String direction)
	{
		this.direction = direction;
	}
}