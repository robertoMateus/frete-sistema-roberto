-- tabela de cliente
CREATE TABLE cliente (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(150) NOT NULL,
    nome_fantasia VARCHAR(150),
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(20),

    logradouro VARCHAR(150),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    municipio VARCHAR(100),
    uf VARCHAR(2),
    cep VARCHAR(10),

    telefone VARCHAR(20),
    email VARCHAR(150),

    status VARCHAR(20) NOT NULL
);

-- tabela de motorista
CREATE TABLE motorista (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(20),

    cnh_numero VARCHAR(20) NOT NULL,
    cnh_categoria VARCHAR(2) NOT NULL,
    cnh_validade DATE NOT NULL,

    tipo_vinculo VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- tabela de veículo
CREATE TABLE veiculo (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    rntrc VARCHAR(20),
    ano_fabricacao INTEGER,
    tipo VARCHAR(50),
    tara_kg NUMERIC(10,2),


    capacidade_kg NUMERIC(10,2),
    volume_m3 NUMERIC(10,2),

    status VARCHAR(20) NOT NULL
);

-- Sequence para geração do número do frete
CREATE SEQUENCE seq_frete_numero START 1;

-- tabela de frete
CREATE TABLE frete (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(30) NOT NULL UNIQUE,
    id_remetente BIGINT NOT NULL,
    id_destinatario BIGINT NOT NULL,
    id_motorista BIGINT NOT NULL,
    id_veiculo BIGINT NOT NULL,
    municipio_origem VARCHAR(100),
    uf_origem VARCHAR(2),
    municipio_destino VARCHAR(100),
    uf_destino VARCHAR(2),
    descricao_carga TEXT,
    peso_kg NUMERIC(10,2),
    volumes INTEGER,
    valor_frete NUMERIC(10,2),
    aliquota_icms NUMERIC(5,2),
    valor_icms NUMERIC(10,2),
    valor_total NUMERIC(10,2),
    status VARCHAR(30) NOT NULL,
    data_emissao TIMESTAMP,
    data_previsao_entrega TIMESTAMP,
    data_saida TIMESTAMP,
    data_entrega TIMESTAMP
);

-- tabela de ocorrências do frete
CREATE TABLE ocorrencia_frete (
    id BIGSERIAL PRIMARY KEY,
    id_frete  BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    municipio VARCHAR(100),
    uf VARCHAR(2),
    descricao TEXT,
    nome_recebedor VARCHAR(150),
    documento_recebedor VARCHAR(20)
);

--tabela de usuario pra autenticação
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE manutencao_veiculo (
    id BIGSERIAL PRIMARY KEY,
    id_veiculo BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    descricao TEXT,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    custo NUMERIC(10,2)
);


-- tabela de preço de rota
CREATE TABLE tabela_frete (
    id BIGSERIAL PRIMARY KEY,
    municipio_origem VARCHAR(100) NOT NULL,
    uf_origem VARCHAR(2) NOT NULL,
    municipio_destino VARCHAR(100) NOT NULL,
    uf_destino VARCHAR(2) NOT NULL,
    valor_base NUMERIC(10,2) NOT NULL,
    valor_por_kg NUMERIC(10,2)
);