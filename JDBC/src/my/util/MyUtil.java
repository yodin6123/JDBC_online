package my.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyUtil {  // 자주 사용하는 기능 추출 ==> 모듈화
	
	public static String getDay(int n) {
		
		Calendar currentDate = Calendar.getInstance();  // 현재 날짜와 시간을 얻어온다.
		currentDate.add(Calendar.DATE, n);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		return dateFormat.format(currentDate.getTime());
		
	}

}
