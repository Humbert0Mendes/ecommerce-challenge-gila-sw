CREATE TABLE orders (
    id          BIGSERIAL    PRIMARY KEY,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    CONSTRAINT chk_orders_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);

CREATE TABLE order_items (
    id          BIGSERIAL       PRIMARY KEY,
    order_id    BIGINT          NOT NULL,
    product_id  BIGINT          NOT NULL,
    quantity    INTEGER         NOT NULL,
    unit_price  NUMERIC(12, 2)  NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT,
    CONSTRAINT chk_order_items_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_order_items_unit_price_non_negative CHECK (unit_price >= 0)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
CREATE INDEX idx_orders_created_at_desc ON orders (created_at DESC);
