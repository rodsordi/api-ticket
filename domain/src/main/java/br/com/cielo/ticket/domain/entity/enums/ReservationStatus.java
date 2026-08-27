package br.com.cielo.ticket.domain.entity.enums;

import br.com.cielo.commons.exception.BusinessException;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status da Reserva de Ingressos", example = "REQUESTED", enumAsRef = true)
public enum ReservationStatus {

    @Schema(description = "Reserva solicitada pelo cliente")
    REQUESTED {
        @Override
        public ReservationStatus markAwaitingPayment() {
            return AWAITING_PAYMENT;
        }

        @Override
        public ReservationStatus cancel() {
            return CANCELED;
        }

        @Override
        public ReservationStatus expire() {
            return EXPIRED;
        }
    },
    @Schema(description = "Aguardando confirmação de pagamento")
    AWAITING_PAYMENT {
        @Override
        public ReservationStatus pay() {
            return PAYED;
        }

        @Override
        public ReservationStatus cancel() {
            return CANCELED;
        }

        @Override
        public ReservationStatus expire() {
            return EXPIRED;
        }
    },
    @Schema(description = "Reserva paga e confirmada")
    PAYED {
        @Override
        public ReservationStatus cancel() {
            return CANCELED;
        }
    },
    @Schema(description = "Reserva cancelada")
    CANCELED,

    @Schema(description = "Reserva expirada por falta de pagamento")
    EXPIRED;

    public boolean isPending() {
        return this == REQUESTED || this == AWAITING_PAYMENT;
    }

    public ReservationStatus markAwaitingPayment() {
        throw new BusinessException(String.format("Transição inválida: não é possível alterar para AWAITING_PAYMENT a partir do estado %s", this));
    }

    public ReservationStatus pay() {
        throw new BusinessException(String.format("Transição inválida: não é possível alterar para PAYED a partir do estado %s", this));
    }

    public ReservationStatus cancel() {
        throw new BusinessException(String.format("Transição inválida: não é possível CANCELAR a partir do estado %s", this));
    }

    public ReservationStatus expire() {
        throw new BusinessException(String.format("Transição inválida: não é possível alterar para EXPIRED a partir do estado %s", this));
    }
}
