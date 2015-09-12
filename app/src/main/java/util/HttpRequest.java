package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http 요청을 도와주는 유틸리티 클래스
 */
public class HttpRequest
{
	/**
	 * 서버로 Get이나 Post 요청을 하는 메서드
	 * @param urlString     요청 URL
	 * @param requestMethod 요청 방식 "GET" or "POST"
	 * @return 요청 결과
	 */
	public static StringBuffer request(String urlString, String requestMethod)
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