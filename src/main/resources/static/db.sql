create table role (
    role_id bigint not null auto_increment,
    role enum('ROLE_ADMIN', 'ROLE_USER'),
    primary key (role_id)
);

insert into role (role) values ('ROLE_ADMIN'), ('ROLE_USER');

create table user (
    id bigint not null auto_increment,
    email varchar(50),
    password varchar(100),
    first_name varchar(50),
    last_name varchar(50),
    address_id bigint,
    phone varchar(20),
    gender enum('M', 'F'),
    is_email_verified boolean,
    created_at timestamp,
    updated_at timestamp,
    primary key (id)
);

create table user_authority (
    user_id bigint not null,
    role_id bigint not null,
    constraint fk_user_authority_user foreign key (user_id) references user(id),
    constraint fk_user_authority_role foreign key (role_id) references role(role_id)
);

create table address (
    address_id bigint not null auto_increment,
    city varchar(100) not null,
    district varchar(100) not null,
    ward varchar(100) not null,
    street varchar(100) not null,
    number varchar(10) not null,
    primary key (address_id)
);

create table product (
    product_id bigint not null auto_increment,
    name varchar(50),
    price float,
    description TEXT,
    created_at timestamp,
    updated_at timestamp,
    primary key (product_id)
);

create table review (
    review_id bigint not null auto_increment,
    review TEXT ,
    product_id bigint not null,
    user_id bigint not null,
    created_at timestamp,
    updated_at timestamp,
    primary key (review_id)
);

create table product_quantity (
    product_id bigint not null,
    quantity bigint not null
);

create table orders (
    order_id bigint not null auto_increment,
    user_id bigint not null ,
    total_cost float not null ,
    order_at timestamp ,
    status enum('IN_DELIVERING', 'DELIVERED'),
    primary key (order_id),
    constraint fk_order_user foreign key (user_id) references user (id)
);

create table order_product (
    order_product_id bigint not null auto_increment,
    order_id bigint not null,
    product_id bigint not null,
    product_quantity int,
    price float,
    primary key (order_product_id),
    constraint fk_order_product_order foreign key (order_id) references orders(order_id),
    constraint fk_order_product_product foreign key (product_id) references product(product_id)
);

create table product_image (
    product_image_id bigint not null auto_increment,
    product_id bigint not null ,
    name varchar(255),
    type varchar(255),
    data longblob,
    primary key (product_image_id)
);

alter table user
    add constraint fk_user_address foreign key (address_id) references address (address_id);

alter table review
    add constraint fk_review_product foreign key (product_id) references product(product_id),
    add constraint fk_review_user foreign key (user_id) references user(id);

alter table product_image
    add constraint fk_product_image_product foreign key (product_id) references product(product_id);

create table refresh_token (
    id bigint auto_increment not null ,
    token varchar(100) not null unique ,
    expiry_date timestamp not null ,
    user_id bigint not null ,
    refresh_count bigint,
    primary key (id),
    constraint fk_refresh_token_user foreign key (user_id) references user (id)
);

create table email_verification_token (
   token_id bigint auto_increment not null ,
   token varchar(100) not null unique ,
   user_id bigint not null ,
   token_status enum('STATUS_PENDING', 'STATUS_CONFIRMED'),
   expiry_date timestamp not null ,
   created_at timestamp,
   updated_at timestamp,
   primary key (token_id),
   constraint fk_email_verification_token_user foreign key (user_id) references user (id)
);
