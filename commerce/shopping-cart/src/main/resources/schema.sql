
CREATE TABLE IF NOT EXISTS shopping_carts
(
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS shopping_carts_products
(
    cart_id UUID NOT NULL REFERENCES shopping_carts(shopping_cart_id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY(cart_id, product_id)
);
