package data;

import android.os.AsyncTask;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
	 * @param listener 중간 경로를 전달받기 위한 리스너
	 */
	public static void request(final String start, final String end, final PathReceivedListener listener)
	{
		new AsyncTask<Void, Void, ArrayList<Movement>>()
		{
			@Override
			protected ArrayList<Movement> doInBackground(Void... voids)
			{
				StringBuffer result = httpRequest("http://m.map.daum.net/actions/walkRoute?startLoc=" + start + "&sxEnc=LVNSRL&syEnc=QNOMOMV&endLoc=" + end + "&exEnc=LVONUO&eyEnc=QNLNSRS&ids=P11124718%2CP10955757&service=", "GET");
				Source source = new Source(result);
				Element root = source.getElementById("daumContent").getFirstElementByClass("list_content_wrap").getFirstElementByClass("list_section list_walk");
				List<Element> elements = root.getAllElements(HTMLElementName.LI);

				ArrayList<Movement> movements = new ArrayList<Movement>();

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
					movements.add(new Movement(x, y, description, direction));
				}

				return movements;
			}

			@Override
			protected void onPostExecute(ArrayList<Movement> result)
			{
				// 리스너로 결과를 전달한다.
				if (listener != null)
					listener.onPathReceived(result);
			}
		}.execute();
	}

	/**
	 * 서버로 Get이나 Post 요청을 하는 메서드
	 * @param urlString     요청 URL
	 * @param requestMethod 요청 방식 "GET" or "POST"
	 * @return 요청 결과
	 */
	private static StringBuffer httpRequest(String urlString, String requestMethod)
	{
		StringBuffer chaine = new StringBuffer("");
		try
		{
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "");
			connection.setRequestMethod(requestMethod);
			connection.setDoInput(true);
			connection.connect();

			InputStream inputStream = connection.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			while ((line = rd.readLine()) != null)
			{
				chaine.append(line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return chaine;
	}
}