CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE products (
    id          BIGSERIAL      PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    sku         VARCHAR(20)    NOT NULL,
    description TEXT           NOT NULL,
    category    VARCHAR(100)   NOT NULL,
    price       NUMERIC(12, 2) NOT NULL,
    stock       INTEGER        NOT NULL,
    weight_kg   NUMERIC(10, 3) NOT NULL,
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_products_sku UNIQUE (sku),
    CONSTRAINT chk_products_price_non_negative CHECK (price >= 0),
    CONSTRAINT chk_products_stock_non_negative CHECK (stock >= 0),
    CONSTRAINT chk_products_weight_non_negative CHECK (weight_kg >= 0)
);

CREATE INDEX idx_products_active_category ON products (category) WHERE active = TRUE;
CREATE INDEX idx_products_name_trgm ON products USING GIN (name gin_trgm_ops) WHERE active = TRUE;
CREATE INDEX idx_products_description_trgm ON products USING GIN (description gin_trgm_ops) WHERE active = TRUE;

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_products_updated_at
BEFORE UPDATE ON products
FOR EACH ROW EXECUTE FUNCTION set_updated_at();
