import React from 'react';
import '../styles/components/IACard.scss';

interface IACardProps {
    onClose: () => void;
    onConfirm: () => void;
}

const IACard: React.FC<IACardProps> = ({ onClose, onConfirm }) => {
    return (
        <div className="modal-glass-box ai-instruction-modal">
            <h2>Teste a Zotion IA :D</h2>
            <div className="instruction-content">
                <p>Extraia lembretes automaticamente de cronogramas e fotos.</p>
                <ul>
                    <li>Lê PDFs e Imagens.</li>
                    <li>Detecta datas de entrega.</li>
                </ul>
            </div>
            <div className="modal-actions">
                <button className="cancel-btn" onClick={onClose}>Agora não</button>
                <button className="confirm-ai-btn" onClick={onConfirm}>Selecionar Arquivo</button>
            </div>
        </div>
    );
};

export default IACard;
