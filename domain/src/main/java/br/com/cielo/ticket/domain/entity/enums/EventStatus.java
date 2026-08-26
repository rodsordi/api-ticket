package br.com.cielo.ticket.domain.entity.enums;

import br.com.cielo.commons.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status do Evento", example = "OPENED_FOR_SALE", enumAsRef = true)
public enum EventStatus {

    @Schema(description = "Aguardando data de lançamento das vendas")
    WAITING_LAUNCHING_DATE {
        @Override
        public EventStatus openForSale() {
            return OPENED_FOR_SALE;
        }
    },
    @Schema(description = "Aberto para vendas de ingressos")
    OPENED_FOR_SALE {
        @Override
        public EventStatus markOutOfStock() {
            return OUT_OF_STOCK;
        }

        @Override
        public EventStatus close() {
            return CLOSED;
        }
    },
    @Schema(description = "Ingressos esgotados")
    OUT_OF_STOCK {
        @Override
        public EventStatus reopen() {
            return REOPENED;
        }

        @Override
        public EventStatus close() {
            return CLOSED;
        }
    },
    @Schema(description = "Vendas de ingressos reabertas")
    REOPENED {
        @Override
        public EventStatus markOutOfStock() {
            return OUT_OF_STOCK;
        }

        @Override
        public EventStatus close() {
            return CLOSED;
        }
    },
    @Schema(description = "Evento encerrado")
    CLOSED;

    public EventStatus openForSale() {
        throw new BusinessException(String.format("Transição inválida: não é possível ABRIR PARA VENDA a partir do estado %s", this));
    }

    public EventStatus markOutOfStock() {
        throw new BusinessException(String.format("Transição inválida: não é possível marcar SEM ESTOQUE a partir do estado %s", this));
    }

    public EventStatus reopen() {
        throw new BusinessException(String.format("Transição inválida: não é possível REABRIR a partir do estado %s", this));
    }

    public EventStatus close() {
        throw new BusinessException(String.format("Transição inválida: não é possível FECHAR a partir do estado %s", this));
    }
}
