/*
     프로그램이 실행되면 
     모든 데이터를 조회해서 보여주도록 한다.
   
-------------------------------------------
글번호	글쓴이	글내용	작성일자
-------------------------------------------
15	권오윤	아 너무 배고파 죽겠따!!!	2020-08-26 16:17:01
14	수정빌런	쉬는시간 리필해주세요..	2020-08-26 12:08:41
13	감자탕	감자탕은 영동!	2020-08-26 12:07:10
12	박수빈	야호야옹오오오	2020-08-26 12:06:44
11	링딩동	링↗딩동↘링→딩동 링디리디리디리딩→딩→딩↘	2020-08-26 12:06:15
9	신호연	테스트^_______^~~~~	2020-08-26 12:05:55
8	박수빈	야호야호야호 감자탕	2020-08-26 12:05:46
7	최지훈	하하하	2020-08-26 12:05:23
5	박수빈	감자탕은 맛있어 짜릿해	2020-08-26 12:05:05
4	이지은	안녕하세요	2020-08-26 12:05:03
3	ㅎㅎㅎ	배고파요~~~~~	2020-08-26 12:05:01
2	서강사님	점심뭐드시겠습니까	2020-08-26 12:04:43 

------ >>> 조회할 대상 <<< -------
1.글번호   2.글쓴이   3.글내용   4.종료
-------------------------------
▷ 선택번호: 1
▷ 검색어:  7

-------------------------------------------
글번호	글쓴이	글내용	작성일자
-------------------------------------------
 7	최지훈	하하하	2020-08-26 12:05:23

      
------ >>> 조회할 대상 <<< -------
1.글번호   2.글쓴이   3.글내용   4.종료
-------------------------------
▷ 선택번호: 2
▷ 검색어: 박수빈

-------------------------------------------
글번호	글쓴이	글내용	작성일자
-------------------------------------------      
12	박수빈	야호야옹오오오	2020-08-26 12:06:44
5	박수빈	감자탕은 맛있어 짜릿해	2020-08-26 12:05:05

------ >>> 조회할 대상 <<< -------
1.글번호   2.글쓴이   3.글내용   4.종료
-------------------------------
▷ 선택번호: 3
▷ 검색어: 배고파     

-------------------------------------------
글번호	글쓴이	글내용	작성일자
------------------------------------------- 
15	권오윤	아 너무 배고파 죽겠따!!!	2020-08-26 16:17:01
3	ㅎㅎㅎ	배고파요~~~~~	2020-08-26 12:05:01


------ >>> 조회할 대상 <<< -------
1.글번호   2.글쓴이   3.글내용   4.종료
-------------------------------
▷ 선택번호: 4

~~~~ 프로그램 종료 ~~~~ 
      
*/

package test01.jdbc;

import java.sql.*;
import java.util.Scanner;

public class DQL_select_where {

	public static void main(String[] args) {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		Scanner sc = new Scanner(System.in);
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			System.out.print("▷ 연결할 오라클 서버의 IP 주소: "); 
			String ip = sc.nextLine();
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:xe", "HR", "cclass"); 
			
			String sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					+ " from jdbc_tbl_memo "
					+ " order by no desc "; 
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();                           
			
			System.out.println("-------------------------------------------");
			System.out.println("글번호\t글쓴이\t글내용\t작성일자");
			System.out.println("-------------------------------------------");
			
			StringBuilder sb = new StringBuilder();
			
			while(rs.next()) {
				
				int no = rs.getInt(1);  
			    String name = rs.getString(2);
			    String msg = rs.getString(3);
			    String writeday = rs.getString("WRITEDAY");
				
			    sb.append(no);
			    sb.append("\t"+name);
			    sb.append("\t"+msg);
				sb.append("\t"+writeday+"\n");
				
			}// end of while(rs.next())------------------------------
			
			System.out.println(sb.toString());
			
			////////////////////////////////////////////////////////////
			
			sb = new StringBuilder();
			sb.append("------ >>> 조회할 대상 <<< -------\n");
			sb.append("1.글번호   2.글쓴이   3.글내용   4.종료\n");
			sb.append("-------------------------------\n");
			String menu = sb.toString();
			
			String menuNo = "";
			do {
				System.out.println(menu);
				System.out.print("▷ 선택번호: ");
				menuNo = sc.nextLine();
				
				String colName = "";  // where 절에 들어올 컬럼명.
				
				switch (menuNo) {
					case "1": // 글번호
						colName = "no";
						break;
						
					case "2": // 글쓴이 
						colName = "name";
						break;	
						
					case "3": // 글내용
						colName = "msg";
						break;	
						
					case "4":
						
						break;						
	
					default:
						System.out.println("~~~ 메뉴에 없는 번호 입니다 ~~~\n");
						break;
				}// end of switch()--------------------
				
				if("1".equals(menuNo) || "2".equals(menuNo) || "3".equals(menuNo)) {
					System.out.print("▷ 검색어: ");
					String search = sc.nextLine();
					
					sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
						+ " from jdbc_tbl_memo ";
					
					if( !"3".equals(menuNo) ) { // 글번호 또는 글쓴이로 검색시 
						sql += " where "+ colName + "= ? ";
					}
					else { // 글내용으로 검색시
						sql += " where "+ colName + " like '%'|| ? ||'%' " ;
					}
					
					sql += " order by no desc ";
					
					
					/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					    위치홀더(== ?)에는 오로지 데이터값만 들어와야 하고, 
					    컬럼명 또는 테이블명에는 위치홀더(== ?)가 들어오면 오류가 발생한다.
					    컬럼명 또는 테이블명은 변수로 처리해주어야 한다. 
					   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!     
					*/
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, search);
					
					rs = pstmt.executeQuery();
					
					System.out.println("-------------------------------------------");
					System.out.println("글번호\t글쓴이\t글내용\t작성일자");
					System.out.println("-------------------------------------------");
					
					// === StringBuilder 초기화 하기 === //
					// sb.length(); // StringBuilder sb 에 append 되어진 개수 ==> 3 
					// sb.delete(0, sb.length());
					// 또는
					sb.setLength(0);
					
					while(rs.next()) {
						
						int no = rs.getInt(1);
						String name = rs.getString(2);
						String msg = rs.getString(3);
						String writeday = rs.getString(4);
						
						sb.append(no);
					    sb.append("\t"+name);
					    sb.append("\t"+msg);
						sb.append("\t"+writeday+"\n");
					}// end of while-------------------------
					
					System.out.println(sb.toString());
					
				}
				
			} while ( !("4".equals(menuNo) ));
			
		} catch (ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다.");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			try {
				if(rs != null)    rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null)  conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}

		sc.close();
		
		System.out.println("~~~ 프로그램 종료 ~~~");	

	}// end of main()----------------------------------

}
