package data;

import android.os.AsyncTask;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.util.ArrayList;
import java.util.List;

import util.HttpRequest;

/**
 * 길찾기 중간경로를 가져오는 도우미 클래스
 */
public class PathRequester
{
	/**
	 * 리스너
	 */
	public interface PathReceivedListener
	{
		/**
		 * 중간경로를 가져왔을 때 호출되는 메서드
		 * @param movements 중간 경로 목록
		 */
		void onPathReceived(ArrayList<Movement> movements);
	}

	/**
	 * 길찾기를 수행하는 메서드
	 * @param start    출발지 이름
	 * @param end      도착지 이름
	 * @param method   대중교통인지 도보인지
	 * @param listener 중간 경로를 전달받기 위한 리스너
	 */
	public static void request(final String start, final String end, final String method, final PathReceivedListener listener)
	{
		new AsyncTask<Void, Void, ArrayList<Movement>>()
		{
			@Override
			protected ArrayList<Movement> doInBackground(Void... voids)
			{
				String startHash = HashPath.hash(start);
				String endHash = HashPath.hash(end);

				if (startHash == null || endHash == null)
					return null;

				String[] startCoor = startHash.split("/");
				String[] endCoor = endHash.split("/");

				ArrayList<Movement> movements = new ArrayList<>();
				if (method.equals("walk"))
				{
					StringBuffer result = HttpRequest.request("http://m.map.daum.net/actions/walkRoute?sxEnc=" + startCoor[0] + "&syEnc=" + startCoor[1] +
							"&exEnc=" + endCoor[0] + "&eyEnc=" + endCoor[1] + "&service=", "GET");
					Source source = new Source(result);
					Element root = source.getFirstElementByClass("list_section list_walk");
					List<Element> elements = root.getAllElements(HTMLElementName.LI);

					for (Element i : elements)
					{
						double x = Double.parseDouble(i.getAttributeValue("data-x"));
						double y = Double.parseDouble(i.getAttributeValue("data-y"));

						Element contents = i.getFirstElementByClass("link_section");
						Element descElement = contents.getFirstElementByClass("txt_section");

						// 이동 방법에 대한 정보가 없으면
						int flag = 1;
						if (descElement == null)
						{
							descElement = contents.getFirstElementByClass("txt_point");
							flag = 0;
						}

						String description = descElement.getTextExtractor().toString();
						String direction = contents.getAllElementsByClass("ico_path").get(flag).getTextExtractor().toString();
						movements.add(new WalkMovement(x, y, description, direction));
					}
				}
				else if (method.equals("public"))
				{
					StringBuffer result = HttpRequest.request("http://m.map.daum.net/actions/publicDetailRoute?mode=list&sxEnc=" + startCoor[0] + "&syEnc=" + startCoor[1] +
							"&exEnc=" + endCoor[0] + "&eyEnc=" + endCoor[1] + "&ranking=1", "GET");
					Source source = new Source(result);
					Element root = source.getFirstElementByClass("list_section list_detail");
					List<Element> elements = root.getAllElements(HTMLElementName.LI);

					for (Element i : elements)
					{
						String data_sx = i.getAttributeValue("data-sx");
						String data_sy = i.getAttributeValue("data-sy");

						if (data_sx == null || data_sx.equals(""))
							data_sx = "0.0";
						if (data_sy == null || data_sy.equals(""))
							data_sy = "0.0";

						double x = Double.parseDouble(data_sx);
						double y = Double.parseDouble(data_sy);

						Element descElement = i.getFirstElementByClass("txt_station");
						Element busNameElement = i.getFirstElementByClass("numGREEN");
						Element busIdElement = i.getFirstElementByClass("move_bus_detail");

						String description = "";
						if (descElement != null && descElement.getTextExtractor() != null)
							description = descElement.getTextExtractor().toString();

						// 버스에 대한 정보가 없으면
						if (busNameElement == null || busIdElement == null)
						{
							movements.add(new WalkMovement(x, y, description, ""));
						}

						// 버스에 대한 정보가 있으면 버스 정보를 추가한다.
						else
						{
							String busName = busNameElement.getTextExtractor().toString();
							String busId = busIdElement.getAttributeValue("data-id");
							movements.add(new BusMovement(x, y, description, busId, busName));
						}
					}
				}

				return movements;
			}

			@Override
			protected void onPostExecute(ArrayList<Movement> result)
			{
				// 리스너로 결과를 전달한다.
				if (listener != null && result != null)
					listener.onPathReceived(result);
			}
		}.execute();
	}
}