package kr.isteam.whitestick;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import data.Movement;
import data.PathRequester;
import util.BluetoothSerialClient;
import util.HttpRequest;

/**
 * 메인 액티비티
 */
public class MainActivity extends AppCompatActivity implements LocationListener
{
	private ListView listMovement;
	private ArrayAdapter<Movement> adapter;
	private TextToSpeech tts;
	private EditText editStart;
	private EditText editEnd;
	private LocationManager locManager;

	/**
	 * 메인 액티비티가 생성될 때 호출되는 메서드
	 * @param savedInstanceState 저장된 인스턴스 상태
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2500L, 0, this);

		editStart = (EditText) findViewById(R.id.edit_start);
		editEnd = (EditText) findViewById(R.id.edit_end);

		BluetoothSerialClient bluetooth = BluetoothSerialClient.getInstance();
		if (bluetooth == null)
		{
			Toast.makeText(this, "블루투스를 초기화하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		if (!bluetooth.isEnabled())
		{
			Toast.makeText(this, "블루투스를 켜주세요.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		// 블루투스 장비를 탐색한다.
		scanDevices();

		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener()
		{
			@Override
			public void onInit(int status)
			{
				// TextToSpeech 엔진의 초기화가 완료되어 사용할 수 있도록 준비된 상태인 경우
				if (status == TextToSpeech.SUCCESS)
				{
					// 음의 높이와 속도를 설정한다.
					tts.setPitch(1.1F);
					tts.setSpeechRate(1);

					// 언어를 한국어로 설정한다.
					tts.setLanguage(Locale.KOREA);

					editStart.requestFocus();
				}
			}
		});

		// 리스트뷰 어댑터
		adapter = new ArrayAdapter<Movement>(this, R.layout.list_item_work_movement)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
					convertView = getItem(position).toView(getLayoutInflater());
				else
					getItem(position).initView(convertView);

				return convertView;
			}
		};
		listMovement = (ListView) findViewById(R.id.list_movements);
		listMovement.setAdapter(adapter);

		View.OnFocusChangeListener editListener = new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(final View v, boolean hasFocus)
			{
				if (hasFocus)
				{
					((EditText) v).setText("");
					String caption = ((EditText) v).getHint().toString();

					tts.speak(caption + "를 말씀해주십시오.", TextToSpeech.QUEUE_FLUSH, null);
					try
					{
						Thread.sleep(1500);
					}
					catch (InterruptedException e)
					{
					}
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

					SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
					recognizer.setRecognitionListener(new RecognitionListener()
					{
						@Override
						public void onReadyForSpeech(Bundle bundle)
						{

						}

						@Override
						public void onBeginningOfSpeech()
						{

						}

						@Override
						public void onRmsChanged(float v)
						{

						}

						@Override
						public void onBufferReceived(byte[] bytes)
						{

						}

						@Override
						public void onEndOfSpeech()
						{

						}

						@Override
						public void onError(int i)
						{

						}

						@Override
						public void onResults(Bundle results)
						{
							String key = "";
							key = SpeechRecognizer.RESULTS_RECOGNITION;
							ArrayList<String> mResult = results.getStringArrayList(key);
							String[] rs = new String[mResult.size()];
							mResult.toArray(rs);
							((EditText) v).setText("" + rs[0]);

							if (v.getId() == R.id.edit_start)
								editEnd.requestFocus();
						}

						@Override
						public void onPartialResults(Bundle bundle)
						{

						}

						@Override
						public void onEvent(int i, Bundle bundle)
						{

						}
					});
					recognizer.startListening(intent);
				}
			}
		};
		editStart.setOnFocusChangeListener(editListener);
		editEnd.setOnFocusChangeListener(editListener);
	}

	/**
	 * 메인 액티비티가 종료될 때 호출되는 메서드
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (tts != null)
			tts.shutdown();

		BluetoothSerialClient client = BluetoothSerialClient.getInstance();
		if (client != null)
			client.clear();

		locManager.removeUpdates(this);
	}

	/**
	 * 사용자가 버튼을 눌렀을 때 호출되는 메서드
	 * @param view 눌린 버튼
	 */
	public void onClick(View view)
	{
		// 어떤 버튼이 눌렸는지 조사한다.
		switch (view.getId())
		{
		case R.id.btn_public:
		case R.id.btn_search: // 검색 버튼
			{
				adapter.clear();
				adapter.notifyDataSetChanged();

				EditText editStart = (EditText) findViewById(R.id.edit_start);
				EditText editEnd = (EditText) findViewById(R.id.edit_end);

				// 길찾기를 시작한다.
				PathRequester.request(editStart.getText().toString(), editEnd.getText().toString(), (view.getId() == R.id.btn_search) ? "walk" : "public",
						new PathRequester.PathReceivedListener()
				{
					/**
					 * 길찾기 결과를 수신하는 메서드
					 * @param movements 중간 경로 목록
					 */
					@Override
					public void onPathReceived(ArrayList<Movement> movements)
					{
						// 리스트뷰를 갱신한다.
						adapter.addAll(movements);
						adapter.notifyDataSetChanged();

						if (tts != null)
							tts.stop();

						tts.speak("길찾기를 시작합니다.", TextToSpeech.QUEUE_FLUSH, null);
					}
				});
			}
			break;

		case R.id.btn_scan: // 블루투스 스캔 버튼
			scanDevices();
			break;
		}
	}

	private void scanDevices()
	{
		final BluetoothSerialClient bluetooth = BluetoothSerialClient.getInstance();
		if (bluetooth == null)
			return;

		bluetooth.scanDevices(this, new BluetoothSerialClient.OnScanListener()
		{
			@Override
			public void onStart()
			{
				MainActivity.this.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(MainActivity.this, "블루투스 장비를 검색합니다.", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onFoundDevice(BluetoothDevice bluetoothDevice)
			{
 				if (!"ISTeam10000001".equals(bluetoothDevice.getName()) && !"ISTeam".equals(bluetoothDevice.getName()))
					return;

				Toast.makeText(MainActivity.this, bluetoothDevice + "와 연결합니다.", Toast.LENGTH_SHORT).show();
				bluetooth.connect(MainActivity.this, bluetoothDevice, new BluetoothSerialClient.BluetoothStreamingHandler()
				{
					@Override
					public void onError(Exception e)
					{
						Log.i("알림", "연결에 실패했습니다.");
//								Toast.makeText(MainActivity.this, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onConnected()
					{
						Log.i("알림", "연결되었습니다.");
						Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
						vibrator.vibrate(1000);

						BluetoothSerialClient client = BluetoothSerialClient.getInstance();
						if (client != null)
							client.cancelScan(MainActivity.this);

						MainActivity.this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								Toast.makeText(MainActivity.this, "연결되었습니다.", Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onDisconnected()
					{
						MainActivity.this.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								Toast.makeText(MainActivity.this, "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();

								// 다시 스캔한다.
								scanDevices();
							}
						});
						Log.i("알림", "연결이 해제되었습니다.");

//								Toast.makeText(MainActivity.this, "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
					}

					private Queue<Byte> byteQueue = new LinkedList<>();
					private ArrayList<String> deplicatedCheck = new ArrayList<String>();
					private String lastNFCCode = "";

					@Override

					public void onData(byte[] buffer, int length)
					{
						try
						{
							int limit;
							for (limit = 0; limit < 8; limit++)
							{
								if ('0' <= buffer[limit] && buffer[limit] <= '9')
									byteQueue.add(buffer[limit]);
								else
									break;
							}

							if (byteQueue.size() >= 8)
							{
								final ByteArrayOutputStream baos = new ByteArrayOutputStream();
								for (int i = 0; i < 8; i++)
									baos.write(byteQueue.poll());

								new Thread()
								{
									public void run()
									{
										Log.i("알림", "데이터 수신");
										String fff = new String(baos.toByteArray(), 0, 8);
										if (fff.equals(lastNFCCode))
											return;
										lastNFCCode = fff;
//										for (String j : deplicatedCheck)
//										{
//											if (j.equals(fff))
//												return;
//										}
										deplicatedCheck.add(fff);
//										StringBuffer response = HttpRequest.request("http://www.naver.com/", "GET");
										StringBuffer response = null;
										try
										{
											response = HttpRequest.request("http://133.130.55.218:8000/" + fff, "GET");
											Log.v("응답", response.toString());
											JSONObject json = new JSONObject(response.toString());
											String message = json.getString("message");
											String decode = URLDecoder.decode(message, "UTF-8");
											tts.speak(decode, TextToSpeech.QUEUE_FLUSH, null);
										}
										catch (UnsupportedEncodingException e)
										{
											e.printStackTrace();
										}
										catch (JSONException e)
										{
											e.printStackTrace();
										}

										int i = 10;
										i += 5;
										response = response;
									}
								}.start();
							}
						}

						catch (Exception e)
						{
						}
					}
				});
			}

			@Override
			public void onFinish()
			{
				MainActivity.this.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(MainActivity.this, "블루투스 장비 검색을 중지합니다. ", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	@Override
	public void onLocationChanged(Location location)
	{
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		for (int i = 0; i < adapter.getCount(); i++)
		{
			Movement movement = adapter.getItem(i);
			MapPoint.GeoCoordinate mapPointGeoCoord = MapPoint.mapPointWithWCONGCoord(movement.getX(), movement.getY()).getMapPointGeoCoord();
			Log.v("정보", "latitude : " + mapPointGeoCoord.latitude + "  longitude : " + mapPointGeoCoord.longitude);

			if (Math.abs(mapPointGeoCoord.latitude - latitude) <= 0.0002 && Math.abs(mapPointGeoCoord.longitude - longitude))
			{
				if (i < adapter.getCount())
					tts.speak(adapter.getItem(i + 1).toString(), TextToSpeech.QUEUE_ADD, null);
				else
				{
					tts.speak("목적지에 도달하셨습니다. 길찾기를 종료합니다.", TextToSpeech.QUEUE_ADD, null);
					adapter.clear();
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle)
	{

	}

	@Override
	public void onProviderEnabled(String s)
	{

	}

	@Override
	public void onProviderDisabled(String s)
	{

	}
}