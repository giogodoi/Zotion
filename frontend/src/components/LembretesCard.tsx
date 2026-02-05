import React from 'react';
import type { Lembrete } from '../types';
import '../styles/components/LembretesCard.scss';

interface LembretesCardProps {
    lembrete: Lembrete;
    onClick: (lembrete: Lembrete) => void;
    onToggleStatus: (id: string) => void;
}

const LABEL_MAP: Record<number, string> = { 3: 'ALTA', 2: 'MEDIA', 1: 'BAIXA' };

const LembretesCard: React.FC<LembretesCardProps> = ({ lembrete, onClick, onToggleStatus }) => {
    return (
        <div
            className={`reminder-card ${lembrete.realizada ? 'done' : ''} prio-${lembrete.prioridade}`}
            onClick={() => onClick(lembrete)}
        >
            <div className="info">
                <h4>{lembrete.nome} <span className="prio-tag">{LABEL_MAP[lembrete.prioridade]}</span></h4>
                <p className="preview-text">{lembrete.descricao || "Sem descrição"}</p>
                <small>Data: {new Date(lembrete.dataHora).toLocaleDateString('pt-BR')}</small>
            </div>
            <div className="action-side">
                <div
                    className={`check-circle ${lembrete.realizada ? 'checked' : ''}`}
                    onClick={(e) => {
                        e.stopPropagation();
                        onToggleStatus(lembrete.id);
                    }}
                />
            </div>
        </div>
    );
};

export default LembretesCard;
