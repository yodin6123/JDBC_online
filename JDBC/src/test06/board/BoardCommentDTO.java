package test06.board;

public class BoardCommentDTO {
	
	private int commentno;      // 댓글번호 
	private String fk_boardno;  // 원글의 글번호 
	private String fk_userid;   // 사용자ID
	private String contents;    // 댓글내용 
	private String writeday;    // 작성일자
	
	private MemberDTO member;   // select용 (jdbc_member 테이블과 JOIN 하기 위해 필요하다)
	
	public int getCommentno() {
		return commentno;
	}
	public void setCommentno(int commentno) {
		this.commentno = commentno;
	}
	public String getFk_boardno() {
		return fk_boardno;
	}
	public void setFk_boardno(String fk_boardno) {
		this.fk_boardno = fk_boardno;
	}
	public String getFk_userid() {
		return fk_userid;
	}
	public void setFk_userid(String fk_userid) {
		this.fk_userid = fk_userid;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getWriteday() {
		return writeday;
	}
	public void setWriteday(String writeday) {
		this.writeday = writeday;
	}
	public MemberDTO getMember() {
		return member;
	}
	public void setMember(MemberDTO member) {
		this.member = member;
	}
	
	// 댓글 내용
	public String commentInfo() {
		String info = contents+"\t"+member.getName()+"\t"+writeday;
		return info;
	}

}
