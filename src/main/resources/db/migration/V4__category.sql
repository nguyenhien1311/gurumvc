drop table if exists category;
drop table if exists beer_category;

create table category
(
    id                 varchar(36) not null Primary key,
    created_date       datetime(6),
    description        varchar(50),
    last_modified_date datetime(6),
    version            integer
) engine = InnoDB;

create table beer_category
(
    category_id varchar(36) not null,
    beer_id     varchar(36) not null,
    primary key (beer_id, category_id),
    constraint pc_beer_id_fk FOREIGN KEY (beer_id) references beer(id),
    constraint pc_category_id_fk FOREIGN KEY (category_id) references category(id)
) engine = InnoDB;
