-- The products.cost column was created as NUMERIC on databases that ran V1 before it was
-- edited to declare DOUBLE PRECISION, which matches the entity (Product.cost is a Double).
-- Hibernate's schema validation on startup fails against any database still carrying the
-- old column type; this migration brings existing installations in line with V1's current
-- (and the entity's actual) type, without requiring a destructive volume reset.
ALTER TABLE maintenance.products
    ALTER COLUMN cost TYPE DOUBLE PRECISION USING cost::double precision;
