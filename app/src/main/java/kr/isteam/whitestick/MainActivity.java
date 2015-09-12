package kr.isteam.whitestick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
	}
}