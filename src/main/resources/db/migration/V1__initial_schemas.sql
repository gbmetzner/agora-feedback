create table "user" (
    id bigint generated always as identity primary key,
    name varchar(255) not null
);

create table category (
    id bigint generated always as identity primary key,
    name varchar(255) not null unique
);

create table feedback (
    id bigint primary key,
    title varchar(255) not null,
    description text not null,
    status varchar(25) not null check (status in ('PENDING', 'ACKNOWLEDGED', 'IN_PROGRESS', 'COMPLETED')),
    author_id bigint references "user"(id) on delete set null,
    category_id bigint references category(id) on delete set null,
    sentiment varchar(50),
    tags varchar(500),
    created_at timestamp with time zone not null,
    archived boolean not null default false
);

create index idx_feedback_status on feedback(status);
create index idx_feedback_created_at on feedback(created_at);
create index idx_feedback_author_id on feedback(author_id);
create index idx_feedback_category_id on feedback(category_id);
create index idx_feedback_archived on feedback(archived);