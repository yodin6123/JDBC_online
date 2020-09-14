package test06.board;

import java.sql.*;

import test05.singleton.dbconnection.MyDBConnection;

public class BoardDAO implements InterBoardDAO {
	
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	// 자원반납을 해주는 메소드 생성하기(자원생성의 역순으로 반납) //
	private void close() {
		try {
			if(rs != null) {
				rs.close();
				rs = null;
			}
			
			if(pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			// Connection은 싱글톤이기 때문에 전체 프로그램 종료 시에 반납해줘야 하므로 따로 반납하지 않는다.
		} catch (SQLException e) {
			
		}
	}// end of close()

	// 게시판 글쓰기(jdbc_board 테이블에 insert)
	@Override
	public int write(BoardDTO bdto) {
		int result = 0;
		
		try {
			conn = MyDBConnection.getConn();
			
			String sql = "insert into jdbc_board(boardno, fk_userid, subject, contents, boardpasswd)\n"+
					"values(board_seq.nextval, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bdto.getFk_userid());
			pstmt.setString(2, bdto.getSubject());
			pstmt.setString(3, bdto.getContents());
			pstmt.setString(4, bdto.getBoardpasswd());
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	}

}
