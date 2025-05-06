
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

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID NOT NULL,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS booking_products
(
    booking_id  UUID NOT NULL REFERENCES bookings(booking_id)           ON DELETE CASCADE,
    product_id  UUID NOT NULL REFERENCES warehouse_products(product_id) ON DELETE CASCADE,
    quantity    BIGINT NOT NULL
);