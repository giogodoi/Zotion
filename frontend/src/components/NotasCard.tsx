import React from 'react';
import type { Nota } from '../types';
import '../styles/components/NotasCard.scss';

interface NotasCardProps {
    nota: Nota;
    defaultColor?: string;
    onClick: (nota: Nota) => void;
    onToggleStatus: (id: string, currentStatus: boolean) => void;
}

const NotasCard: React.FC<NotasCardProps> = ({ nota, defaultColor = '#2c2c2e', onClick, onToggleStatus }) => {
    return (
        <div
            className={`note-glass-card ${nota.realizada ? 'completed' : ''}`}
            style={{ borderLeft: `6px solid ${nota.cor || defaultColor}` }}
            onClick={() => onClick(nota)}
        >
            <div className="card-header">
                <h4>{nota.titulo}</h4>
                <button
                    className="check-btn"
                    onClick={(e) => { e.stopPropagation(); onToggleStatus(nota.id, !!nota.realizada); }}
                >
                    {nota.realizada ? 'Reabrir' : 'Concluir'}
                </button>
            </div>
            <p className="preview-content">{nota.conteudo}</p>
        </div>
    );
};

export default NotasCard;
