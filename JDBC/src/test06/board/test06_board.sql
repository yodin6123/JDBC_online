show user;

-- USER이(가) "MYORAUSER"입니다.

        ------ ***  회원 테이블 생성하기  *** ------
drop table jdbc_member purge; 
        
create table jdbc_member
(userseq       number        not null    -- 회원번호
,userid        varchar2(30)  not null    -- 회원아이디
,passwd        varchar2(30)  not null    -- 회원암호
,name          varchar2(20)  not null    -- 회원명
,mobile        varchar2(20)              -- 연락처
,point         number(10) default 0      -- 포인트
,registerday   date default sysdate      -- 가입일자 
,status        number(1) default 1       -- status 컬럼의 값이 1 이면 정상, 0 이면 탈퇴 
,constraint PK_jdbc_member primary key(userseq)
,constraint UQ_jdbc_member unique(userid)
,constraint CK_jdbc_member check( status in(0,1) )
,constraint CK_jdbc_member_point check( point < 30 )
);

select *
from jdbc_member;

drop sequence userseq;

create sequence userseq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


        ------ ///  게시판 테이블 생성하기  /// ------
--drop table jdbc_board purge;
        
create table jdbc_board
(boardno       number        not null          -- 글번호
,fk_userid     varchar2(30)  not null          -- 작성자아이디
,subject       varchar2(100) not null          -- 글제목
,contents      varchar2(200) not null          -- 글내용
,writeday      date default sysdate not null   -- 작성일자
,viewcount     number default 0 not null       -- 조회수 
,boardpasswd   varchar2(20) not null           -- 글암호 
,constraint PK_jdbc_board primary key(boardno)
,constraint FK_jdbc_board foreign key(fk_userid) references jdbc_member(userid) 
);

select *
from jdbc_board;

--drop sequence board_seq;

create sequence board_seq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


        ------ ///  댓글 테이블 생성하기  /// ------
--drop table jdbc_comment purge;
        
create table jdbc_comment 
(commentno   number        not null    -- 댓글번호 
,fk_boardno  number        not null    -- 원글의 글번호 
,fk_userid   varchar2(30)  not null    -- 사용자ID
,contents    varchar2(200) not null    -- 댓글내용 
,writeday    date default sysdate      -- 작성일자
,constraint  PK_jdbc_comment  primary key(commentno) 
,constraint  FK_jdbc_comment_fk_boardno foreign key(fk_boardno) 
             references jdbc_board(boardno) on delete cascade 
,constraint  FK_jdbc_comment_fk_userid  foreign key(fk_userid) 
             references jdbc_member(userid) 
);

select *
from jdbc_comment;

--drop sequence seq_comment;

create sequence seq_comment
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

-----------------------------------------------------------------------------------
select *
from jdbc_member
order by userseq;


select userseq, userid, passwd, name, mobile, point
     , to_char(registerday, 'yyyy-mm-dd') AS registerday, status 
from jdbc_member 
where userid = 'leess' and passwd = '1234';
