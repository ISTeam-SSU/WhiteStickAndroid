package data;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import kr.isteam.whitestick.R;

/** 이동경로를 나타내는 클래스 */
public class WalkMovement extends Movement
{
	private String direction;   // 방향

	/**
	 * 생성자
	 * @param x
	 * @param y
	 * @param description
	 * @param direction
	 */
	public WalkMovement(double x, double y, String description, String direction)
	{
		super(x, y, description);
		this.direction = direction;
	}

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(String direction)
	{
		this.direction = direction;
	}

	@Override
	public View toView(LayoutInflater inflater)
	{
		View view = inflater.inflate(R.layout.list_item_work_movement, null);
		initView(view);

		return view;
	}

	@Override
	public void initView(View view)
	{
		TextView txtCoordinate = (TextView) view.findViewById(R.id.txt_coordinate);
		TextView txtDescription = (TextView) view.findViewById(R.id.txt_description);
		TextView txtDirection = (TextView) view.findViewById(R.id.txt_direction);

		txtCoordinate.setText(String.format("좌표 : x(%f), y(%f)", getX(), getY()));
		txtDescription.setText("설명 : " + getDescription());
		txtDirection.setText("방향 : " + getDirection());
	}

	@Override
	public String toString()
	{
		String message = "";
		if (!getDirection().equals(getDescription()))
			message = getDirection() + ", " + getDescription();
		else
			message = getDescription();
		return message;
	}
}