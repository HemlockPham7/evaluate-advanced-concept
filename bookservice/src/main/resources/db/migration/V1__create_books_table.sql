CREATE TABLE IF NOT EXISTS public.books (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50),
    price NUMERIC(19, 2),
    status VARCHAR(255),
    content VARCHAR(255),
    category VARCHAR(255),
    author VARCHAR(50)
);

ALTER TABLE public.books REPLICA IDENTITY FULL;

CREATE PUBLICATION books_publication FOR TABLE public.books;