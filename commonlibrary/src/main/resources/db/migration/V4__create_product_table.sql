CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    price NUMERIC(19, 2),
    status VARCHAR(255),
    category VARCHAR(255)
);