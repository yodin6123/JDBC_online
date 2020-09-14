package test01.jdbc;

import java.sql.*;
import java.util.Scanner;

public class DML_delete {

	public static void main(String[] args) {

		Connection conn = null;
		// Connection conn 은 오라클 데이터베이스 서버와 연결을 맺어주는 객체이다.
		
		PreparedStatement pstmt = null;
		// Connection conn(특정 오라클서버)에 전송할 SQL문(편지)을 전달할 객체(우편배달부)이다. 
		
		ResultSet rs = null;
		// ResultSet rs 은 select 되어진 결과물이 저장되어지는 곳 

		Scanner sc = new Scanner(System.in);
		
		try {
			// >>> 1. 오라클 드라이버 로딩 <<<  //
			/*
			   === OracleDriver(오라클 드라이버)의 역할 ===
			   1). OracleDriver 를 메모리에 로딩시켜준다.
			   2). OracleDriver 객체를 생성해준다.
			   3). OracleDriver 객체를 DriverManager에 등록시켜준다.
			       --> DriverManager 는 여러 드라이버들을 Vector 에 저장하여 관리해주는 클래스이다.
			*/
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			
			// >>> 2. 어떤 오라클 서버에 연결을 할래? <<<  //
			System.out.print("▷ 연결할 오라클 서버의 IP 주소: "); 
			String ip = sc.nextLine();
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@"+ip+":1521:xe", "HR", "cclass"); 
			
			conn.setAutoCommit(false); // 수동 commit 으로 전환
			
			// >>> 3. SQL문(편지)을 작성한다 <<<  //
			String sql = " select no, name, msg, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday "
					+ " from jdbc_tbl_memo "
					+ " order by no desc "; 
			
			
			// >>> 4. 연결한 오라클서버(conn)에 SQL문(편지)을 전달할 PreparedStatement 객체(우편배달부) 생성하기 <<<  // 
			pstmt = conn.prepareStatement(sql);
			
			
			// >>> 5. PreparedStatement pstmt 객체(우편배달부)가 작성된 SQL문(편지)을 오라클서버에 보내서 실행이 되도록 한다 <<< // 
			rs = pstmt.executeQuery();  // sql 문이 select 이므로 executeQuery() 이다.
            // select 되어진 결과물은 ResultSet rs 에 들어온다.	                         
			
			System.out.println("-------------------------------------------");
			System.out.println("글번호\t글쓴이\t글내용\t작성일자");
			System.out.println("-------------------------------------------");
			
			StringBuilder sb = new StringBuilder();
			
			while(rs.next()) {
				/*
				   rs.next() 는 select 되어진 결과물에서 커서의 위치(행의 위치)를 
				      다음으로 옮긴 후 행이 존재하면 true, 행이 없으면 false 를 리턴시켜준다.    
				*/
				
				int no = rs.getInt(1);  // 숫자 1은 select 되어서 나오는 컬럼의 위치값(순서) 이다. 즉, no 컬럼을 말하는 것이다.
			// 또는
			//	int no = rs.getInt("no"); // "no"는 no 컬럼명을 말한다.
				
			    String name = rs.getString(2);
			    String msg = rs.getString(3);
			    String writeday = rs.getString("WRITEDAY");
				
			    sb.append(no);
			    sb.append("\t"+name);
			    sb.append("\t"+msg);
				sb.append("\t"+writeday+"\n");
				
			}// end of while(rs.next())------------------------------
			
			System.out.println(sb.toString());
			
			/////////////////////////////////////////////////////////
			System.out.print("▷ 삭제할 글번호: ");
			String no = sc.nextLine();
			
			sql = " delete from jdbc_tbl_memo "
				+ " where no = ? ";
			
			pstmt.close();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, no); // 1 은 sql에서 첫번째 ? 를 말한다. 첫번째 ? 에 no 를 넣어라. 
			
			int n = pstmt.executeUpdate();
			
			if(n==1) {
				
				String yn = "";
				do {
					////////////////////////////////////////////////
					System.out.print("▷ 정말로 삭제하시겠습니까?[Y/N] ");
					yn = sc.nextLine();
					
					if("y".equalsIgnoreCase(yn)) {
						conn.commit();  // 커밋
						System.out.println(">> 데이터 삭제 성공!! <<");
					}
					else if("n".equalsIgnoreCase(yn)) {
						conn.rollback(); // 롤백
						System.out.println(">> 데이터 삭제 취소!! <<");
					}
					else {
						System.out.println(">> Y 또는 N 만 입력하세요!! <<\n");
					}

			    } while( !("y".equalsIgnoreCase(yn) || "n".equalsIgnoreCase(yn) ) ); 
				
			}
			
			else if(n==0) {
				System.out.println(">> 삭제하시려는 글번호 "+no+"는 존재하지 않는 글번호 입니다. <<");
			}
			
			else {
				System.out.println(">> 데이터 삭제에 오류가 발생함 <<");
			}
			
		} catch (ClassNotFoundException e) {
			System.out.println(">> ojdbc6.jar 파일이 없습니다.");
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// >>> 6. 사용하였던 자원 반납하기 <<< //
			// 반납의 순서는 생성순서의 역순으로 한다.
			
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
		
	}// end of main()------------------------------------------

}
