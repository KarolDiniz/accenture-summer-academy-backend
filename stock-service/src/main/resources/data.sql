-- Inserir produtos
INSERT INTO products (id, name, sku, description, price)
VALUES
    (1, 'Produto Padrão', 'SKU-001', 'Descrição do Produto Padrão', 99.99),
    (2, 'Produto Secundário', 'SKU-002', 'Descrição do Produto Secundário', 149.99);

-- Inserir estoque para os produtos com relacionamento correto
INSERT INTO stock (product_id, quantity, version)
VALUES
    (1, 100, 0),
    (2, 50, 0);