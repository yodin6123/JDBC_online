show user;
-- USER이(가) "HR"입니다.

create table jdbc_tbl_memo
(no        number(4)
,name      varchar2(20) not null
,msg       varchar2(200) not null
,writeday  date default sysdate
,constraint PK_jdbc_tbl_memo_no primary key(no)
);

create sequence jdbc_seq_memo
start with 1
increment by 1 
nomaxvalue
nominvalue
nocycle
nocache;

select *
from jdbc_tbl_memo
order by no desc;


 select *  
 from jdbc_tbl_memo  
 order by no desc;

 select *
 from jdbc_tbl_memo
 where no = 9;
 
 select *
 from jdbc_tbl_memo
 where no = '9';
 
 
 update jdbc_tbl_memo set name = 'sfsfd'
 where no = 2423;
 -- 0개 행 이(가) 업데이트되었습니다.






