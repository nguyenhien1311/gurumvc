
    alter table beer_order 
       add column order_shipment_id varchar(36);

    alter table beer_order_line 
       add column order_id varchar(36);

    create table beer_order_shipment (
       id varchar(36) not null,
        created_date datetime(6),
        last_modified_date datetime(6),
        tracking_number varchar(255),
        version integer,
        order_id varchar(36),
        primary key (id)
    ) engine=InnoDB;

    alter table beer_order 
       add constraint FKcqqoeu0lpab46viyfbv4b3ygv 
       foreign key (order_shipment_id) 
       references beer_order_shipment (id);

    alter table beer_order_line
        add constraint FKlgv4wmhxtd83hodv8fyilbs41
            foreign key (order_id)
                references beer_order (id);

    alter table beer_order_shipment 
       add constraint FKarhhnswpti5621itpa6icxl37 
       foreign key (order_id) 
       references beer_order (id);
