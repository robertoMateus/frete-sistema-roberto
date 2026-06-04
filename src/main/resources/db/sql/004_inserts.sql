--insert de usuários
INSERT INTO usuario (nome, login, senha, ativo) VALUES
('Administrador', 'admin', 'admin', TRUE),
('Operador Logística', 'operador', 'op123', TRUE),
('Supervisor', 'supervisor', 'sup123', TRUE);

-- insert de clientes
INSERT INTO cliente (razao_social, nome_fantasia, cnpj, inscricao_estadual, logradouro, numero, complemento, bairro, municipio, uf, cep, telefone, email, status) VALUES
('Distribuidora Norte Ltda', 'DistriNorte', '11222333000181', '123456789', 'Rua das Palmeiras', '100', 'Galpão A', 'Distrito Industrial', 'Recife', 'PE', '50000000', '(81) 99999-1111', 'contato@distrinorte.com.br', 'ATIVO'),
('Comercial Sul S.A.', 'ComSul', '22333444000172', '987654321', 'Av. Paulista', '1500', 'Sala 302', 'Bela Vista', 'São Paulo', 'SP', '01310100', '(11) 98888-2222', 'comercial@comsul.com.br', 'ATIVO'),
('Indústria Centro Oeste Ltda', 'IndCentro', '33444555000163', '456789123', 'Rua do Cerrado', '250', NULL, 'Setor Industrial', 'Goiânia', 'GO', '74000000', '(62) 97777-3333', 'industria@indcentro.com.br', 'ATIVO'),
('Atacado Nordeste S.A.', 'AtacaNE', '44555666000154', '321654987', 'Av. Sete de Setembro', '800', 'Depósito 2', 'Centro', 'Fortaleza', 'CE', '60000000', '(85) 96666-4444', 'atacado@atacane.com.br', 'ATIVO'),
('Transportes Leste Ltda', 'TransLeste', '55666777000145', '654321098', 'Rua da Bahia', '300', NULL, 'Comércio', 'Salvador', 'BA', '40000000', '(71) 95555-5555', 'contato@transleste.com.br', 'ATIVO'),
('Supermercados Rio Ltda', 'SuperRio', '66777888000136', '789012345', 'Av. Rio Branco', '200', 'Loja 1', 'Centro', 'Rio de Janeiro', 'RJ', '20040020', '(21) 94444-6666', 'compras@superrio.com.br', 'ATIVO'),
('Frigorífico Sul Ltda', 'FrigoSul', '77888999000127', '012345678', 'Rua das Indústrias', '500', NULL, 'Distrito Industrial', 'Porto Alegre', 'RS', '90000000', '(51) 93333-7777', 'frigorifico@frigosul.com.br', 'INATIVO');


-- insert de motoristas
INSERT INTO motorista (nome, cpf, data_nascimento, telefone, cnh_numero, cnh_categoria, data_validade_cnh, tipo_vinculo, status) VALUES
('Carlos Eduardo Silva', '52998224725', '1985-03-15', '(81) 99111-1111', '12345678901', 'E', '2027-03-15', 'FUNCIONARIO', 'ATIVO'),
('José Antônio Santos', '11144477735', '1978-07-22', '(81) 98222-2222', '23456789012', 'D', '2026-07-22', 'FUNCIONARIO', 'ATIVO'),
('Pedro Henrique Oliveira', '66168741609', '1990-11-10', '(81) 97333-3333', '34567890123', 'E', '2027-11-10', 'AGREGADO', 'ATIVO'),
('Marcos Antônio Costa', '60593124420', '1982-05-28', '(81) 96444-4444', '45678901234', 'C', '2027-05-28', 'TERCEIRO', 'ATIVO'),
('Luiz Fernando Alves', '19119118000', '1975-09-03', '(81) 95555-5555', '56789012345', 'E', '2026-09-03', 'FUNCIONARIO', 'ATIVO'),
('Roberto Carlos Souza', '48478291557', '1988-01-17', '(81) 94666-6666', '67890123456', 'D', '2024-01-17', 'AGREGADO', 'SUSPENSO');

-- insert de veículos
INSERT INTO veiculo (placa, rntrc, ano_fabricacao, tipo, tara_kg, capacidade_kg, volume_m3, status) VALUES
('ABC1234', '12345678', 2018, 'CARRETA', 8500.00, 27000.00, 90.00, 'DISPONIVEL'),
('DEF5678', '23456789', 2020, 'TRUCK', 6000.00, 14000.00, 45.00, 'EM_VIAGEM'),
('GHI9012', '34567890', 2019, 'CARRETA', 9000.00, 25000.00, 85.00, 'EM_VIAGEM'),
('JKL3456', '45678901', 2021, 'VAN', 2000.00, 3500.00, 15.00, 'EM_VIAGEM'),
('MNO7890', '56789012', 2017, 'TRUCK', 5800.00, 13000.00, 42.00, 'EM_MANUTENCAO'),
('PQR1234', '67890123', 2022, 'UTILITARIO', 1500.00, 1000.00, 6.00, 'DISPONIVEL'),
('ABC1E23', '78901234', 2023, 'CARRETA', 8800.00, 28000.00, 92.00, 'DISPONIVEL');

-- insert de fretes
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo, municipio_origem, uf_origem, municipio_destino, uf_destino, descricao_carga, peso_kg, volumes, valor_frete, aliquota_icms, valor_icms, valor_total, status, data_emissao, data_previsao_entrega, data_saida, data_entrega) VALUES

('FRT-2026-00001', 1, 2, 1, 1, 'Recife', 'PE', 'São Paulo', 'SP', 'Eletrônicos diversos', 5000.00, 200, 3500.00, 12.00, 420.00, 3920.00, 'ENTREGUE', '2026-05-01 08:00:00', '2026-05-05 18:00:00', '2026-05-01 10:00:00', '2026-05-05 14:30:00'),

('FRT-2026-00002', 3, 4, 2, 2, 'Goiânia', 'GO', 'Fortaleza', 'CE', 'Produtos alimentícios', 8000.00, 350, 4200.00, 12.00, 504.00, 4704.00, 'EM_TRANSITO', '2026-05-10 09:00:00', '2026-05-15 18:00:00', '2026-05-10 11:00:00', NULL),

('FRT-2026-00003', 5, 6, 3, 3, 'Salvador', 'BA', 'Rio de Janeiro', 'RJ', 'Móveis e eletrodomésticos', 12000.00, 80, 5800.00, 12.00, 696.00, 6496.00, 'SAIDA_CONFIRMADA', '2026-05-20 07:00:00', '2026-05-25 18:00:00', '2026-05-20 09:30:00', NULL),

('FRT-2026-00004', 2, 3, 1, 1, 'São Paulo', 'SP', 'Goiânia', 'GO', 'Peças automotivas', 2000.00, 120, 2100.00, 12.00, 252.00, 2352.00, 'EMITIDO', '2026-05-25 10:00:00', '2026-05-30 18:00:00', NULL, NULL),

('FRT-2026-00005', 4, 1, 5, 6, 'Fortaleza', 'CE', 'Recife', 'PE', 'Materiais de construção', 800.00, 50, 900.00, 12.00, 108.00, 1008.00, 'CANCELADO', '2026-05-18 08:00:00', '2026-05-22 18:00:00', NULL, NULL),

('FRT-2026-00006', 6, 7, 5, 7, 'Rio de Janeiro', 'RJ', 'Porto Alegre', 'RS', 'Produtos têxteis', 6000.00, 300, 4800.00, 12.00, 576.00, 5376.00, 'NAO_ENTREGUE', '2026-05-05 08:00:00', '2026-05-10 18:00:00', '2026-05-05 10:00:00', NULL),

('FRT-2026-00007', 1, 5, 4, 6, 'Recife', 'PE', 'Salvador', 'BA', 'Equipamentos industriais', 15000.00, 40, 6500.00, 12.00, 780.00, 7280.00, 'EMITIDO', '2026-05-28 09:00:00', '2026-06-02 18:00:00', NULL, NULL),

('FRT-2026-00008', 2, 4, 4, 4,'São Paulo', 'SP','Fortaleza', 'CE','Medicamentos',3000.00,150,2800.00,12.00,336.00,3136.00,'EM_TRANSITO','2026-05-10 08:00:00','2026-05-15 18:00:00','2026-05-10 10:00:00',NULL);
-- insert de ocorrências
INSERT INTO ocorrencia_frete (id_frete, tipo, data_hora, municipio, uf, descricao, nome_recebedor, documento_recebedor) VALUES
(1, 'SAIDA_PATIO', '2026-05-01 10:00:00', 'Recife', 'PE', NULL, NULL, NULL),
(1, 'EM_ROTA', '2026-05-02 14:00:00', 'Maceió', 'AL', NULL, NULL, NULL),
(1, 'EM_ROTA', '2026-05-03 10:00:00', 'Salvador', 'BA', NULL, NULL, NULL),
(1, 'ENTREGA_REALIZADA', '2026-05-05 14:30:00', 'São Paulo', 'SP', NULL, 'João da Silva', '123.456.789-00'),

(2, 'SAIDA_PATIO', '2026-05-10 11:00:00', 'Goiânia', 'GO', NULL, NULL, NULL),
(2, 'EM_ROTA', '2026-05-11 16:00:00', 'Palmas', 'TO', NULL, NULL, NULL),
(2, 'EM_ROTA', '2026-05-13 09:00:00', 'Teresina', 'PI', NULL, NULL, NULL),

(3, 'SAIDA_PATIO', '2026-05-20 09:30:00', 'Salvador', 'BA', NULL, NULL, NULL),
(6, 'SAIDA_PATIO', '2026-05-05 10:00:00', 'Rio de Janeiro', 'RJ', NULL, NULL, NULL),
(6, 'EM_ROTA', '2026-05-06 15:00:00', 'São Paulo', 'SP', NULL, NULL, NULL),
(6, 'TENTATIVA_ENTREGA', '2026-05-09 11:00:00', 'Porto Alegre', 'RS', 'Destinatário ausente no endereço informado.', NULL, NULL),
(8, 'SAIDA_PATIO','2026-05-10 10:00:00','São Paulo','SP',NULL,NULL,NULL),
(8, 'EM_ROTA','2026-05-11 16:00:00','Belo Horizonte','MG',NULL,NULL,NULL),
(8, 'EM_ROTA','2026-05-13 14:00:00','Salvador','BA',NULL,NULL,NULL);

-- insert de manutenções
INSERT INTO manutencao_veiculo (id_veiculo, tipo, descricao, data_inicio, data_fim, custo) VALUES
(5, 'preventiva', 'Troca de óleo e filtros', '2026-05-15', '2026-05-16', 850.00),
(5, 'corretiva', 'Substituição de pneus dianteiros', '2026-05-17', NULL, 2400.00),
(1, 'preventiva', 'Revisão geral 100.000 km', '2026-04-10', '2026-04-11', 1200.00),
(3, 'corretiva', 'Reparo no sistema de freios', '2026-03-20', '2026-03-22', 1800.00);

-- insert de preços de rota
INSERT INTO preco_rota (municipio_origem, uf_origem, municipio_destino, uf_destino, valor_base, valor_por_kg) VALUES
('Recife', 'PE', 'São Paulo', 'SP', 2500.00, 0.50),
('Recife', 'PE', 'Rio de Janeiro', 'RJ', 2000.00, 0.45),
('Recife', 'PE', 'Salvador', 'BA', 800.00, 0.20),
('Recife', 'PE', 'Fortaleza', 'CE', 600.00, 0.15),
('São Paulo', 'SP', 'Rio de Janeiro', 'RJ', 700.00, 0.10),
('São Paulo', 'SP', 'Goiânia', 'GO', 1200.00, 0.30),
('São Paulo', 'SP', 'Porto Alegre', 'RS', 1500.00, 0.35),
('Goiânia', 'GO', 'Fortaleza', 'CE', 1800.00, 0.40),
('Salvador', 'BA', 'Rio de Janeiro', 'RJ', 1600.00, 0.38),
('Rio de Janeiro', 'RJ', 'Porto Alegre', 'RS', 1800.00, 0.42);