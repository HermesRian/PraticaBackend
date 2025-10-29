-- Tabela de Contas a Pagar
CREATE TABLE IF NOT EXISTS contas_pagar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Dados da conta
    numero VARCHAR(50) NULL,
    modelo VARCHAR(10) NULL,
    serie VARCHAR(10) NULL,
    parcela INT NOT NULL,

    -- Valores
    valor DECIMAL(10, 2) NOT NULL,
    desconto DECIMAL(10, 2) NULL DEFAULT 0.00,
    multa DECIMAL(10, 2) NULL DEFAULT 0.00,
    juro DECIMAL(10, 2) NULL DEFAULT 0.00,
    valor_baixa DECIMAL(10, 2) NULL,

    -- Relacionamentos (FK)
    fornecedor_id BIGINT NOT NULL,
    forma_pagamento_id BIGINT NULL,
    nota_entrada_id BIGINT NULL,  -- Opcional: vincula com nota de entrada quando aplicável

    -- Datas
    data_vencimento DATE NOT NULL,
    data_emissao DATE NULL,
    data_baixa DATE NULL,
    data_pagamento DATE NULL,
    data_cancelamento DATE NULL,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',

    -- Descrições
    descricao TEXT NULL,
    justificativa_cancelamento TEXT NULL,

    -- Auditoria
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_conta_pagar_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id),
    CONSTRAINT fk_conta_pagar_forma_pagamento FOREIGN KEY (forma_pagamento_id) REFERENCES formas_pagamento(id),
    CONSTRAINT fk_conta_pagar_nota_entrada FOREIGN KEY (nota_entrada_id) REFERENCES notas_entrada(id),

    -- Índices para performance
    INDEX idx_fornecedor_id (fornecedor_id),
    INDEX idx_forma_pagamento_id (forma_pagamento_id),
    INDEX idx_nota_entrada_id (nota_entrada_id),
    INDEX idx_data_vencimento (data_vencimento),
    INDEX idx_data_pagamento (data_pagamento),
    INDEX idx_parcela (parcela),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;