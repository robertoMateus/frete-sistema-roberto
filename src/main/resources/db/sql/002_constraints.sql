--Foreign Keys
ALTER TABLE frete
ADD CONSTRAINT fk_frete_remetente
FOREIGN KEY (id_remetente) REFERENCES cliente(id);

ALTER TABLE frete
ADD CONSTRAINT fk_frete_destinatario
FOREIGN KEY (id_destinatario) REFERENCES cliente(id);

ALTER TABLE frete
ADD CONSTRAINT fk_frete_motorista
FOREIGN KEY (id_motorista) REFERENCES motorista(id);

ALTER TABLE frete
ADD CONSTRAINT fk_frete_veiculo
FOREIGN KEY (id_veiculo) REFERENCES veiculo(id);

ALTER TABLE ocorrencia_frete
ADD CONSTRAINT fk_ocorrencia_frete
FOREIGN KEY (id_frete) REFERENCES frete(id);

ALTER TABLE manutencao_veiculo
ADD CONSTRAINT fk_manutencao_veiculo
FOREIGN KEY (id_veiculo) REFERENCES veiculo(id);

-- Unique Constraints
ALTER TABLE tabela_frete
ADD CONSTRAINT uk_tabela_frete
UNIQUE (municipio_origem, uf_origem, municipio_destino, uf_destino);