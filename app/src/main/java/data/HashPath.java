package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lk on 2015. 9. 12..
 */
class HashPath
{
	public static String hash(String word)
	{
		word = word.replace(" ", "");
		String url = "http://m.map.daum.net/actions/searchView?q=" + word;
		String response = requestHttp(url);

		if ("".equals(response))
			return null;
		else
		{
			StringBuilder sb = new StringBuilder();
			boolean flag = false;
			for (char c : response.toCharArray())
			{
				if ((c >= ':' && c <= 'z' || c == '-'))
				{
					continue;
				}
				else
				{
					sb.append(c);
				}
			}
			String result[] = sb.toString().split("\"");
			return function(result[13] + "") + "/" + function("" + result[15]);
		}
	}

	private static String requestHttp(String urlStr){
		try {
			URL url = new URL(urlStr);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			InputStream is = con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String response = "";
			String tmp = "";
			while( (tmp = reader.readLine())!= null){
				if(tmp.contains("search_item base")) {
					response += tmp;
					break;
				}
			}
			is.close();

			return new String(response);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String function(String abcd){
		String ab = abcd;

		if(abcd == null)
			return null;

		ab = ab + "";
		String c = "432101234";
		int e = 28;
		String a = "";
		int d = "0".charAt(0);
		StringBuilder result = new StringBuilder();
		for(int b = 0; b < ab.length(); ++b){
			a+= result.append( (char)(((c.charAt(b%c.length())-d)^(ab.charAt(b)-d)) + e + d) );
		}

		return result.toString();
	}
}