create table send_request
(id bigint not null auto_increment,
create_date datetime,
person bigint,
room varchar(255),
send_user bigint,
token varchar(3) not null,
total_money bigint,
primary key (id),
index(token));

create table send_detail
(id bigint not null auto_increment,
receive_date datetime,
receive_money bigint,
receive_user varchar(255),
state integer not null,
send_request_id bigint,
primary key (id));

alter table send_detail
add constraint FKeyn2o0519cfc5awpsekh7xq74
foreign key (send_request_id) references send_request (id);