-- Script para inclusão de 52 produtos de Cafeteria no PDV
-- Compatível com PostgreSQL e H2

-- 1. Criação das Categorias da Cafeteria
INSERT INTO categorias (nome) VALUES ('Bebidas Geladas') ON CONFLICT (nome) DO NOTHING;
INSERT INTO categorias (nome) VALUES ('Bebidas Quentes') ON CONFLICT (nome) DO NOTHING;
INSERT INTO categorias (nome) VALUES ('Cafeteria') ON CONFLICT (nome) DO NOTHING;
INSERT INTO categorias (nome) VALUES ('Cafés Gelados') ON CONFLICT (nome) DO NOTHING;
INSERT INTO categorias (nome) VALUES ('Confeitaria & Doces') ON CONFLICT (nome) DO NOTHING;
INSERT INTO categorias (nome) VALUES ('Lanches & Salgados') ON CONFLICT (nome) DO NOTHING;
INSERT INTO categorias (nome) VALUES ('Sucos Naturais') ON CONFLICT (nome) DO NOTHING;

-- 2. Inserção / Atualização dos Produtos (Upsert)
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Café Expresso Tradicional 30ml', '7891000400001', 4.50, 100, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Café Expresso Duplo 60ml', '7891000400002', 7.00, 80, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Café Americano 150ml', '7891000400003', 6.00, 70, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Café Pingado Tradicional 150ml', '7891000400004', 5.50, 90, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Café com Leite Especial 200ml', '7891000400005', 7.50, 85, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Cappuccino Italiano Clássico 150ml', '7891000400006', 8.50, 60, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Cappuccino com Canela e Chocolate 200ml', '7891000400007', 9.50, 65, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Mocaccino com Ganache de Chocolate 200ml', '7891000400008', 11.00, 50, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Flat White com Leite Vaporizado 180ml', '7891000400009', 10.00, 45, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Café Coado V60 Grão Especial 200ml', '7891000400010', 9.00, 40, id, true FROM categorias WHERE nome = 'Cafeteria'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Cold Brew Café Gelado Artesanal 300ml', '7891000400011', 12.00, 35, id, true FROM categorias WHERE nome = 'Cafés Gelados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Iced Latte com Xarope de Baunilha 350ml', '7891000400012', 13.50, 30, id, true FROM categorias WHERE nome = 'Cafés Gelados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Iced Caramel Macchiato 350ml', '7891000400013', 14.00, 35, id, true FROM categorias WHERE nome = 'Cafés Gelados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Espresso Tônica com Limão Siciliano 300ml', '7891000400014', 13.00, 25, id, true FROM categorias WHERE nome = 'Cafés Gelados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Frappuccino de Café com Chocolate 400ml', '7891000400015', 16.50, 40, id, true FROM categorias WHERE nome = 'Cafés Gelados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Frappuccino de Doce de Leite com Chantilly 400ml', '7891000400016', 17.00, 35, id, true FROM categorias WHERE nome = 'Cafés Gelados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Chocolate Quente Cremoso Europeu 200ml', '7891000400017', 10.50, 50, id, true FROM categorias WHERE nome = 'Bebidas Quentes'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Chocolate Quente Especial com Marshmallow 250ml', '7891000400018', 12.50, 40, id, true FROM categorias WHERE nome = 'Bebidas Quentes'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Chá Verde com Hortelã Orgânico 200ml', '7891000400019', 6.50, 60, id, true FROM categorias WHERE nome = 'Bebidas Quentes'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Chá Indiano Masala Chai com Especiarias 250ml', '7891000400020', 9.00, 45, id, true FROM categorias WHERE nome = 'Bebidas Quentes'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Chá Gelado de Pêssego da Casa 350ml', '7891000400021', 8.00, 55, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Suco Natural de Laranja 400ml', '7891000400022', 8.00, 50, id, true FROM categorias WHERE nome = 'Sucos Naturais'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Suco de Melancia com Gengibre e Hortelã 400ml', '7891000400023', 9.50, 35, id, true FROM categorias WHERE nome = 'Sucos Naturais'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Suco Verde Detox (Couve, Abacaxi e Maçã) 400ml', '7891000400024', 10.00, 30, id, true FROM categorias WHERE nome = 'Sucos Naturais'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Soda Italiana de Maçã Verde 350ml', '7891000400025', 11.00, 40, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Soda Italiana de Frutas Vermelhas 350ml', '7891000400026', 11.00, 40, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Água Mineral sem Gás 500ml', '7891000400027', 3.50, 120, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Água Mineral com Gás 500ml', '7891000400028', 4.00, 100, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Água Tônica Antarctica Lata 350ml', '7891000400029', 6.00, 50, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Refrigerante Coca-Cola Lata 350ml', '7891000400030', 6.00, 80, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Refrigerante Coca-Cola Zero Lata 350ml', '7891000400031', 6.00, 70, id, true FROM categorias WHERE nome = 'Bebidas Geladas'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Pão de Queijo Mineiro Tradicional 70g', '7891000400032', 4.50, 90, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Pão de Queijo Recheado com Catupiry 90g', '7891000400033', 6.50, 60, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Croissant Francês Tradicional na Manteiga', '7891000400034', 8.50, 40, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Croissant Recheado com Presunto e Queijo Prato', '7891000400035', 12.50, 35, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Croissant com Peito de Peru e Queijo Brie', '7891000400036', 15.90, 25, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Toast Rústico de Fermentação com Avocado e Ovo Poché', '7891000400037', 18.00, 20, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Tostex Clássico Misto Quente no Pão de Brioche', '7891000400038', 11.50, 45, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Quiche Lorraine (Bacon Crocante e Queijo Gruvère)', '7891000400039', 13.00, 30, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Quiche de Alho Poró com Ricota e Cogumelos', '7891000400040', 13.00, 25, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Empada Artesanal de Palmito Cremoso', '7891000400041', 7.50, 40, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Coxinha Cremosa de Frango com Requeijão Empanada', '7891000400042', 8.00, 50, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Folhado de Frango com Requeijão e Milho', '7891000400043', 8.50, 35, id, true FROM categorias WHERE nome = 'Lanches & Salgados'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Bolo Caseiro de Cenoura com Calda de Brigadeiro (Fatia)', '7891000400044', 8.50, 30, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Bolo Red Velvet com Recheio de Cream Cheese (Fatia)', '7891000400045', 12.50, 20, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Cheesecake com Calda Artesanal de Frutas Vermelhas', '7891000400046', 14.00, 20, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Brownie Tradicional com Nozes Pecan e Chocolate 70%', '7891000400047', 9.50, 40, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Cookie Tradicional Americano com Gotas de Chocolate', '7891000400048', 7.00, 50, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Cookie Red Velvet Recheado com Nutella Pura', '7891000400049', 9.00, 35, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Torta Holandesa Cremosa (Fatia Individual)', '7891000400050', 13.50, 22, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Tiramisù Tradicional Italiano com Queijo Mascarpone', '7891000400051', 15.00, 18, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
INSERT INTO produtos (nome, codigo_barras, preco, estoque, categoria_id, ativo)
SELECT 'Brigadeiro Gourmet Artesanal de Café 30g', '7891000400052', 4.00, 60, id, true FROM categorias WHERE nome = 'Confeitaria & Doces'
ON CONFLICT (codigo_barras) DO UPDATE 
SET nome = EXCLUDED.nome, preco = EXCLUDED.preco, estoque = EXCLUDED.estoque, categoria_id = EXCLUDED.categoria_id, ativo = EXCLUDED.ativo;
