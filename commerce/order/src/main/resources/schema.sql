-- Таблица заказов
CREATE TABLE IF NOT EXISTS orders
(
    order_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username         VARCHAR NOT NULL,
    shopping_cart_id UUID    NOT NULL,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN,
    total_price      DOUBLE PRECISION,
    delivery_price   DOUBLE PRECISION,
    product_price    DOUBLE PRECISION
    );

-- Таблица продуктов в заказе
CREATE TABLE IF NOT EXISTS order_products
(
    order_id  UUID NOT NULL REFERENCES orders (order_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity   INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (order_id, product_id)
    );
