package kr.isteam.whitestick;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import data.Movement;
import data.PathRequester;

/**
 * 메인 액티비티
 */
public class MainActivity extends AppCompatActivity
{
	private ListView listMovement;
	private ArrayAdapter<Movement> adapter;
	private TextToSpeech tts;

	/**
	 * 메인 액티비티가 생성될 때 호출되는 메서드
	 * @param savedInstanceState 저장된 인스턴스 상태
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
				}
			}
		});

		// 리스트뷰 어댑터
		adapter = new ArrayAdapter<Movement>(this, R.layout.list_item_movement)
		{
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				if (convertView == null)
					convertView = getLayoutInflater().inflate(R.layout.list_item_movement, null);
				TextView txtCoordinate = (TextView) convertView.findViewById(R.id.txt_coordinate);
				TextView txtDescription = (TextView) convertView.findViewById(R.id.txt_description);
				TextView txtDirection = (TextView) convertView.findViewById(R.id.txt_direction);

				Movement item = getItem(position);
				txtCoordinate.setText(String.format("좌표 : x(%f), y(%f)", item.getX(), item.getY()));
				txtDescription.setText("설명 : " + item.getDescription());
				txtDirection.setText("방향 : " + item.getDirection());

				return convertView;
			}
		};
		listMovement = (ListView) findViewById(R.id.list_movements);
		listMovement.setAdapter(adapter);
	}

	/**
	 * 메인 액티비티가 종료될 때 호출되는 메서드
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		tts.shutdown();
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
		case R.id.btn_search: // 검색 버튼
			adapter.clear();
			adapter.notifyDataSetChanged();

			// 길찾기를 시작한다.
			PathRequester.request("광화문광장", "서울창조경제혁신센터", new PathRequester.PathReceivedListener()
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

					// TODO 테스트 코드: Movement 경로를 소리내어 읽는다. (삭제할 것)
					for (int i = 0; i < adapter.getCount(); i++)
					{
						Movement item = adapter.getItem(i);
						tts.speak(item.getDirection() + " " + item.getDescription(), TextToSpeech.QUEUE_ADD, null);
					}
				}
			});
			break;
		}
	}
}