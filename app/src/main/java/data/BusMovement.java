package data;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import kr.isteam.whitestick.R;

/**
 * Created by WinAPI on 2015-09-13.
 */
public class BusMovement extends Movement
{
	private String busId;
	private String busName;

	public BusMovement(double x, double y, String description, String busId, String busName)
	{
		super(x, y, description);
		this.busId = busId;
		this.busName = busName;
	}

	@Override
	public View toView(LayoutInflater inflater)
	{
		View view = inflater.inflate(R.layout.list_item_bus_movement, null);
		initView(view);

		return view;
	}

	@Override
	public void initView(View view)
	{
		TextView txtBusId = (TextView) view.findViewById(R.id.txt_bus_id);
		TextView txtBusName = (TextView) view.findViewById(R.id.txt_bus_name);
		TextView txtCoordinate = (TextView) view.findViewById(R.id.txt_coordinate);
		TextView txtDescription = (TextView) view.findViewById(R.id.txt_description);

		txtCoordinate.setText(String.format("좌표 : x(%f), y(%f)", getX(), getY()));
		txtBusId.setText("버스ID : " + getBusId());
		txtBusName.setText("버스 이름 : " + getBusName());
		txtDescription.setText("설명 : " + getDescription());
	}

	@Override
	public String toString()
	{
		return getBusName() + "번 버스, " + getDescription();
	}

	public String getBusId()
	{
		return busId;
	}

	public void setBusId(String busId)
	{
		this.busId = busId;
	}

	public String getBusName()
	{
		return busName;
	}

	public void setBusName(String busName)
	{
		this.busName = busName;
	}
}