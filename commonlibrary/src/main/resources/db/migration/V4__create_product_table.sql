CREATE TABLE IF NOT EXISTS public.products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    price NUMERIC(19, 2),
    status VARCHAR(255),
    category VARCHAR(255)
);

ALTER TABLE public.products REPLICA IDENTITY FULL;

CREATE PUBLICATION cache_publication FOR TABLE public.products;