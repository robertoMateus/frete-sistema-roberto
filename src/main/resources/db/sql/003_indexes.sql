-- Cliente
CREATE INDEX idx_cliente_razao_social ON cliente(razao_social);
CREATE INDEX idx_cliente_status ON cliente(status);

-- Motorista
CREATE INDEX idx_motorista_nome ON motorista(nome);
CREATE INDEX idx_motorista_status ON motorista(status);

-- Veículo
CREATE INDEX idx_veiculo_status ON veiculo(status);

-- Frete
CREATE INDEX idx_frete_status ON frete(status);
CREATE INDEX idx_frete_data_emissao ON frete(data_emissao);
CREATE INDEX idx_frete_id_motorista ON frete(id_motorista);
CREATE INDEX idx_frete_id_veiculo ON frete(id_veiculo);

-- Ocorrência
CREATE INDEX idx_ocorrencia_id_frete ON ocorrencia_frete(id_frete);
CREATE INDEX idx_ocorrencia_data_hora ON ocorrencia_frete(data_hora);