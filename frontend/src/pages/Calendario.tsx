import React, { useState } from 'react';
import type { Lembrete, Nota } from '../types';
import '../styles/calendario.scss';

interface CalendarioProps {
  lembretes: Lembrete[];
  notas: Nota[];
}

const Calendario: React.FC<CalendarioProps> = ({ lembretes, notas }) => {
  const [dataVisualizacao, setDataVisualizacao] = useState(new Date());
  const [eventoSelecionado, setEventoSelecionado] = useState<Lembrete | Nota | null>(null);

  const nomesMeses = [
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
  ];
  
  const ano = dataVisualizacao.getFullYear();
  const mes = dataVisualizacao.getMonth();

  const diasNoMes = new Date(ano, mes + 1, 0).getDate();
  const indexPrimeiroDia = new Date(ano, mes, 1).getDay();
  const dias = Array.from({ length: diasNoMes }, (_, i) => i + 1);
  const espacosVazios = Array.from({ length: indexPrimeiroDia }, (_, i) => i);

  const mudarMes = (offset: number) => {
    setDataVisualizacao(new Date(ano, mes + offset, 1));
  };

  const buscarEventosDoDia = (dia: number) => {
    // Busca Lembretes (usa dataHora do Java)
    const lembretesDoDia = lembretes.filter(l => {
      const d = new Date(l.dataHora);
      return d.getUTCFullYear() === ano && d.getUTCMonth() === mes && d.getUTCDate() === dia;
    });

    // Busca Notas (usa data do Java)
    const notasDoDia = notas.filter(n => {
      if (!n.data) return false;
      const d = new Date(n.data);
      return d.getUTCFullYear() === ano && d.getUTCMonth() === mes && d.getUTCDate() === dia;
    });

    return [...lembretesDoDia, ...notasDoDia];
  };

  return (
    <div className="calendario-page-full">
      <header className="calendario-nav">
        <div className="seletor">
          <button onClick={() => mudarMes(-1)}>❮</button>
          <h2>{nomesMeses[mes]} {ano}</h2>
          <button onClick={() => mudarMes(1)}>❯</button>
        </div>
        <button className="hoje-btn" onClick={() => setDataVisualizacao(new Date())}>Hoje</button>
      </header>

      <div className="calendario-grid-container">
        <div className="dias-semana">
          {['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'].map(d => <div key={d}>{d}</div>)}
        </div>
        
        <div className="grade-dias">
          {espacosVazios.map(s => <div key={`vazio-${s}`} className="dia vazio"></div>)}
          
          {dias.map(dia => {
            const eventos = buscarEventosDoDia(dia);
            return (
              <div key={dia} className="dia">
                <span className="numero-dia">{dia}</span>
                <div className="pontos">
                  {eventos.map((ev, i) => {
                    const ehLembrete = 'prioridade' in ev;
                    return (
                      <div 
                        key={i} 
                        className={`ponto ${ehLembrete ? `prio-${ev.prioridade}` : 'ponto-nota'}`}
                        style={!ehLembrete && ev.cor ? { background: ev.cor } : {}}
                        onClick={() => setEventoSelecionado(ev)}
                      />
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {eventoSelecionado && (
        <div className="modal-overlay" onClick={() => setEventoSelecionado(null)}>
          <div className="modal-glass-box modal-detalhes" onClick={e => e.stopPropagation()}>
            <span className="tag-tipo">
              {'prioridade' in eventoSelecionado ? 'Lembrete' : 'Nota'}
            </span>
            <h3>
              {'nome' in eventoSelecionado ? eventoSelecionado.nome : eventoSelecionado.titulo}
            </h3>
            
            <div className="caixa-conteudo">
              <p>
                {'descricao' in eventoSelecionado ? eventoSelecionado.descricao : eventoSelecionado.conteudo}
              </p>
            </div>
            
            <button onClick={() => setEventoSelecionado(null)}>Fechar</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Calendario;