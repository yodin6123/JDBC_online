package test06.board;

public interface InterBoardDAO {
	
	int write(BoardDTO bdto);  // 게시판 글쓰기(jdbc_board 테이블에 insert)

}
