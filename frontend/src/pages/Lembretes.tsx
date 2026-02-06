import React, { useState, useRef } from 'react';
import api from '../services/api';
import type { Lembrete } from '../types';
import '../styles/lembretes.scss';
import LembretesCard from '../components/LembretesCard';
import IACard from '../components/IACard';

interface LembretesProps {
  lembretes: Lembrete[];
  setLembretes: React.Dispatch<React.SetStateAction<Lembrete[]>>;
}

const PRIORITY_MAP: Record<string, number> = { 'ALTA': 3, 'MEDIA': 2, 'BAIXA': 1 };
const LABEL_MAP: Record<number, string> = { 3: 'ALTA', 2: 'MEDIA', 1: 'BAIXA' };

const Lembretes: React.FC<LembretesProps> = ({ lembretes, setLembretes }) => {
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showAiInstructions, setShowAiInstructions] = useState(false);
  const [selecionado, setSelecionado] = useState<Lembrete | null>(null);
  const [editandoId, setEditandoId] = useState<string | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const [filtro, setFiltro] = useState<'todos' | 'pendentes' | 'concluidos'>('todos');
  const [formData, setFormData] = useState({ titulo: '', descricao: '', data: '', prioridade: 'MEDIA' });

  const fileInputRef = useRef<HTMLInputElement>(null);

  const abrirEdicao = (lembrete: Lembrete) => {
    const dataAjustada = lembrete.dataHora.split('T')[0];

    setFormData({
      titulo: lembrete.nome,
      descricao: lembrete.descricao || '',
      data: dataAjustada,
      prioridade: LABEL_MAP[lembrete.prioridade].replace('É', 'E')
    });

    setEditandoId(lembrete.id);
    setSelecionado(null);
    setShowCreateModal(true);
  };

  const fecharModalCadastro = () => {
    setShowCreateModal(false);
    setEditandoId(null);
    setFormData({ titulo: '', descricao: '', data: '', prioridade: 'MEDIA' });
  };

  const handleFileSelection = () => {
    setShowAiInstructions(false);
    fileInputRef.current?.click();
  };

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const data = new FormData();
    data.append('arquivo', file);

    setIsUploading(true);
    try {
      const response = await api.post('/ia/processar-pdf', data, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setLembretes(prev => [...response.data, ...prev]);
      alert("IA: Lembretes extraídos e salvos!");
    } catch {
      alert("Erro ao processar com IA.");
    } finally {
      setIsUploading(false);
    }
  };

  const alternarStatus = async (id: string) => {
    try {
      const response = await api.patch(`/lembretes/${id}/completar`);
      setLembretes(prev => prev.map(l => l.id === id ? response.data : l));
      if (selecionado?.id === id) setSelecionado(response.data);
    } catch (error) {
      console.error("Erro ao atualizar status:", error);
    }
  };

  const salvarLembrete = async (e: React.FormEvent) => {
    e.preventDefault();
    const payload = {
      nome: formData.titulo,
      descricao: formData.descricao,
      dataHora: `${formData.data}T09:00:00`,
      prioridade: PRIORITY_MAP[formData.prioridade],
      realizada: false
    };

    try {
      if (editandoId) {
        const response = await api.put(`/lembretes/${editandoId}`, payload);
        setLembretes(prev => prev.map(l => l.id === editandoId ? response.data : l));
      } else {
        const response = await api.post('/lembretes', payload);
        setLembretes(prev => [response.data, ...prev]);
      }
      fecharModalCadastro();
    } catch (error) {
      console.error("Erro ao salvar:", error);
    }
  };

  const excluirLembrete = async (id: string) => {
    if (!window.confirm("Deseja excluir este lembrete?")) return;
    try {
      await api.delete(`/lembretes/${id}`);
      setLembretes(prev => prev.filter(l => l.id !== id));
      setSelecionado(null);
    } catch {
      alert("Erro ao excluir.");
    }
  };

  const lembretesOrdenados = [...lembretes].sort((a, b) => b.prioridade - a.prioridade);

  const filtrados = lembretesOrdenados.filter(l => {
    if (filtro === 'pendentes') return !l.realizada;
    if (filtro === 'concluidos') return l.realizada;
    return true;
  });

  return (
    <div className="lembretes-page-full">
      <div className="lembretes-header">
        <div className="ios-tabs">
          <button className={filtro === 'todos' ? 'active' : ''} onClick={() => setFiltro('todos')}>Todos</button>
          <button className={filtro === 'pendentes' ? 'active' : ''} onClick={() => setFiltro('pendentes')}>Pendentes</button>
        </div>

        <div className="actions">
          <input type="file" ref={fileInputRef} style={{ display: 'none' }} onChange={handleFileChange} accept=".pdf,.png,.jpg,.jpeg" />
          <button
            className={`ai-btn ${isUploading ? 'loading' : ''}`}
            onClick={() => setShowAiInstructions(true)}
            disabled={isUploading}
          >
            {isUploading ? 'Processando...' : 'Testar com IA'}
          </button>
          <button className="add-btn" onClick={() => setShowCreateModal(true)}>+ Novo</button>
        </div>
      </div>

      <div className="lembretes-list">
        {filtrados.map(l => (
          <LembretesCard
            key={l.id}
            lembrete={l}
            onClick={setSelecionado}
            onToggleStatus={alternarStatus}
          />
        ))}
      </div>

      {showAiInstructions && (
        <div className="modal-overlay">
          <IACard
            onClose={() => setShowAiInstructions(false)}
            onConfirm={handleFileSelection}
          />
        </div>
      )}

      {showCreateModal && (
        <div className="modal-overlay">
          <form className="modal-glass-box zotion-form" onSubmit={salvarLembrete}>
            <h3>{editandoId ? 'Editar Lembrete' : 'Novo Lembrete'}</h3>
            <div className="input-group">
              <label>Título</label>
              <input className="z-field" value={formData.titulo} onChange={e => setFormData({ ...formData, titulo: e.target.value })} required />
            </div>
            <div className="input-group">
              <label>Descrição</label>
              <textarea className="z-field z-area" value={formData.descricao} onChange={e => setFormData({ ...formData, descricao: e.target.value })} />
            </div>
            <div className="form-row">
              <div className="input-group">
                <label>Data</label>
                <input type="date" className="z-field" value={formData.data} onChange={e => setFormData({ ...formData, data: e.target.value })} required />
              </div>
              <div className="input-group">
                <label>Prioridade</label>
                <select className="z-field" value={formData.prioridade} onChange={e => setFormData({ ...formData, prioridade: e.target.value })}>
                  <option value="ALTA">Alta</option>
                  <option value="MEDIA">Média</option>
                  <option value="BAIXA">Baixa</option>
                </select>
              </div>
            </div>
            <div className="modal-actions">
              <button type="button" onClick={fecharModalCadastro}>Cancelar</button>
              <button type="submit" className="save-btn">{editandoId ? 'Atualizar' : 'Salvar'}</button>
            </div>
          </form>
        </div>
      )}
      {selecionado && (
        <div className="modal-overlay" onClick={() => setSelecionado(null)}>
          <div className="modal-glass-box detail-view" onClick={e => e.stopPropagation()}>
            <div className="detail-header">
              <h2>{selecionado.nome}</h2>
              <div className="header-actions">
                <button className="deletar" onClick={() => excluirLembrete(selecionado.id)}>Excluir</button>
              </div>
            </div>
            <p className="detail-date">Data {new Date(selecionado.dataHora).toLocaleString('pt-BR')}</p>
            <div className="detail-content">{selecionado.descricao || "Sem descrição."}</div>
            <div className="modal-actions">
              <button className={`done-toggle ${selecionado.realizada ? 'reopen' : ''}`} onClick={() => alternarStatus(selecionado.id)}>
                {selecionado.realizada ? 'Reabrir Lembrete' : 'Marcar como Feito'}
              </button>
              <button className="editar" onClick={() => abrirEdicao(selecionado)}>Editar</button>
              <button className="close-btn" onClick={() => setSelecionado(null)}>Fechar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Lembretes;