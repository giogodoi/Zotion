import React, { useState } from 'react';
import api from '../services/api';
import type { Nota } from '../types';
import '../styles/notas.scss';
import NotasCard from '../components/NotasCard';

interface NotasProps {
  notas: Nota[];
  setNotas: React.Dispatch<React.SetStateAction<Nota[]>>;
}

const CORES = ['#2c2c2e', '#007aff', '#34c759', '#ffcc00', '#ff3b30'];

const Notas: React.FC<NotasProps> = ({ notas, setNotas }) => {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [selectedNote, setSelectedNote] = useState<Nota | null>(null);
  const [nova, setNova] = useState({ titulo: '', conteudo: '', cor: CORES[0] });

  const alternarStatus = async (id: string, concluida: boolean) => {
    try {
      const { data } = await api.patch(`/notas/${id}/completar`, { realizada: !concluida });
      setNotas(prev => prev.map(n => n.id === id ? data : n));
      if (selectedNote?.id === id) setSelectedNote(data);
    } catch (err) {
      console.error("Erro ao atualizar nota:", err);
    }
  };

  const salvarNota = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post('/notas', nova);
      setNotas(prev => [res.data, ...prev]);
      setShowCreateModal(false);
      setNova({ titulo: '', conteudo: '', cor: CORES[0] });
    } catch (err) {
      console.error("Erro ao salvar:", err);
    }
  };

  const excluirNota = async (id: string) => {
    if (!window.confirm("Deseja excluir esta nota permanentemente?")) return;
    try {
      await api.delete(`/notas/${id}`);
      setNotas(prev => prev.filter(n => n.id !== id));
      setSelectedNote(null);
    } catch {
      alert("Erro ao excluir nota.");
    }
  };

  return (
    <div className="notes-page-full">
      <div className="notes-header-actions">
        <h2>Minhas Notas</h2>
        <button className="add-note-btn" onClick={() => setShowCreateModal(true)}>Nova Nota</button>
      </div>

      <div className="notes-grid-layout">
        {notas.map(nota => (
          <NotasCard
            key={nota.id}
            nota={nota}
            defaultColor={CORES[0]}
            onClick={setSelectedNote}
            onToggleStatus={alternarStatus}
          />
        ))}
      </div>

      {showCreateModal && (
        <div className="modal-overlay">
          <form className="modal-glass-box zotion-form" onSubmit={salvarNota}>
            <h3>Criar Nova Nota</h3>
            <input
              placeholder="Título"
              value={nova.titulo}
              onChange={e => setNova({ ...nova, titulo: e.target.value })}
              required
            />
            <textarea
              placeholder="Escreva algo..."
              value={nova.conteudo}
              onChange={e => setNova({ ...nova, conteudo: e.target.value })}
              required
            />

            <div className="color-selector">
              <label>Escolha uma cor:</label>
              <div className="dots-container">
                {CORES.map(c => (
                  <div
                    key={c}
                    className={`dot ${nova.cor === c ? 'active' : ''}`}
                    style={{ background: c }}
                    onClick={() => setNova({ ...nova, cor: c })}
                  />
                ))}
              </div>
            </div>

            <div className="modal-actions">
              <button type="button" onClick={() => setShowCreateModal(false)}>Cancelar</button>
              <button type="submit" className="save-btn">Guardar</button>
            </div>
          </form>
        </div>
      )}

      {selectedNote && (
        <div className="modal-overlay" onClick={() => setSelectedNote(null)}>
          <div className="modal-glass-box details-modal" onClick={e => e.stopPropagation()}>
            <div className="details-header">
              <h3>{selectedNote.titulo}</h3>
              <div className="badge" style={{ background: selectedNote.cor }}>Nota</div>
            </div>

            <div className="details-body scrollable">
              <p>{selectedNote.conteudo}</p>
            </div>

            <div className="modal-actions details-actions">
              <button
                type="button"
                className={`mark-done-btn ${selectedNote.realizada ? 'done' : ''}`}
                onClick={() => alternarStatus(selectedNote.id, !!selectedNote.realizada)}
              >
                {selectedNote.realizada ? 'Reabrir' : 'Concluir'}
              </button>
              <button type="button" className="delete-btn" onClick={() => excluirNota(selectedNote.id)}>
                Excluir
              </button>
              <button type="button" onClick={() => setSelectedNote(null)}>Fechar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Notas;