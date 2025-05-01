
CREATE TABLE IF NOT EXISTS products
(
    product_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name     VARCHAR(255) NOT NULL,
    description      TEXT NOT NULL,
    image_src        TEXT,
    quantity_state   VARCHAR(10) NOT NULL,
    product_state    VARCHAR(15) NOT NULL,
    product_category VARCHAR(10),
    price            DOUBLE PRECISION NOT NULL
);
