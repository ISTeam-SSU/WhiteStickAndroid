package kr.isteam.whitestick;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 메인 액티비티
 */
public class MainActivity extends AppCompatActivity
{
	/**
	 * 메인 액티비티가 생성될 때 호출되는 메서드
	 * @param savedInstanceState 저장된 인스턴스 상태
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new AsyncTask<Void, Void, StringBuffer>()
		{
			@Override
			protected StringBuffer doInBackground(Void... voids) {
				return request("http://www.naver.com", "GET");
			}

			@Override
			protected void onPostExecute(StringBuffer result) {
				if (result != null) {
					TextView tv = (TextView) findViewById(R.id.txt);
					tv.setText(result);
				}
			}
		}.execute();
	}

	/**
	 * 서버로 Get 요청을 하는 메서
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
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestProperty("User-Agent", "");
			connection.setRequestMethod(requestMethod);
			connection.setDoInput(true);
			connection.connect();

			InputStream inputStream = connection.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			while ((line = rd.readLine()) != null) {
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