package data;

import android.view.LayoutInflater;
import android.view.View;

/**
 * 중간 경로에 대한 정보
 */
public abstract class Movement
{
	private double x, y;        // 좌표
	private String description; // 설명

	public Movement(double x, double y, String description)
	{
		this.x = x;
		this.y = y;
		this.description = description;
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

	public abstract View toView(LayoutInflater inflater);

	public abstract void initView(View view);

	@Override
	public abstract String toString();
}