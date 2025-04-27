
CREATE TABLE IF NOT EXISTS warehouse_products
(
    product_id  UUID PRIMARY KEY,
    fragile     BOOLEAN,
    width       DOUBLE PRECISION NOT NULL,
    height      DOUBLE PRECISION NOT NULL,
    depth       DOUBLE PRECISION NOT NULL,
    weight      DOUBLE PRECISION NOT NULL,
    quantity    BIGINT NOT NULL

);
