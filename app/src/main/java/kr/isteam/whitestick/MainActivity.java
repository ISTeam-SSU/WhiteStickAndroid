package kr.isteam.whitestick;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

import data.Movement;

/**
 * 메인 액티비티
 */
public class MainActivity extends AppCompatActivity
{
	/**
	 * 메인 액티비티가 생성될 때 호출되는 메서드
	 *
	 * @param savedInstanceState 저장된 인스턴스 상태
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new AsyncTask<Void, Void, ArrayList<Movement>>()
		{
			@Override
			protected ArrayList<Movement> doInBackground(Void... voids)
			{
				StringBuffer result = request("http://m.map.daum.net/actions/walkRoute?startLoc=숭실대학교&sxEnc=LVNSRL&syEnc=QNOMOMV&endLoc=효창공원&exEnc=LVONUO&eyEnc=QNLNSRS&ids=P11124718%2CP10955757&service=", "GET");
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
					int ij = 10;
					ij = ij;
				}

				return movements;
			}

			@Override
			protected void onPostExecute(ArrayList<Movement> result)
			{
				if (result != null)
				{
//					TextView tv = (TextView) findViewById(R.id.txt);
//					tv.setText(result);
				}
			}
		}.execute();
	}

	/**
	 * 서버로 Get 요청을 하는 메서
	 *
	 * @param urlString     요청 URL
	 * @param requestMethod 요청 방식 "GET" or "POST"
	 * @return
	 */
	private StringBuffer request(String urlString, String requestMethod)
	{
		// TODO Auto-generated method stub

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

		} catch (IOException e)
		{
			// writing exception to log
			e.printStackTrace();
		}

		return chaine;
	}
}