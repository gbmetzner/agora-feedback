create table comment (
    id bigint primary key,
    text text not null,
    feedback_id bigint not null references feedback(id) on delete cascade,
    author_id bigint not null references "user"(id) on delete cascade,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    is_developer_response boolean not null default false,
    upvotes int not null default 0
);

create index idx_comment_feedback_id on comment(feedback_id);
create index idx_comment_author_id on comment(author_id);
create index idx_comment_created_at on comment(created_at);