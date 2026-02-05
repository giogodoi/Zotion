import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const Cadastro: React.FC = () => {
  const [dados, setDados] = useState({ nome: '', sobrenome: '', email: '', senha: '' });
  const navigate = useNavigate();

  const lidarComCadastro = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/autenticacao/cadastro', dados);
      alert("Conta criada! Faça seu login.");

      navigate('/');
    } catch {
      alert("Erro ao cadastrar. Tente outro e-mail.");
    }
  };

  return (
    <div className="auth-wrapper">
      <header>
        <div className="logo"><img src="/logo.png" alt="Zotion" /></div>
        <h1>Crie sua conta</h1>
        <p>Já possui conta? <span className="link-destaque" onClick={() => navigate('/')}><strong>Entre aqui</strong></span></p>
      </header>

      <form onSubmit={lidarComCadastro}>
        <input
          placeholder="Nome"
          onChange={e => setDados({ ...dados, nome: e.target.value })}
          required
        />
        <input
          placeholder="Sobrenome"
          onChange={e => setDados({ ...dados, sobrenome: e.target.value })}
          required
        />
        <input
          type="email"
          placeholder="E-mail"
          onChange={e => setDados({ ...dados, email: e.target.value })}
          required
        />
        <input
          type="password"
          placeholder="Senha"
          onChange={e => setDados({ ...dados, senha: e.target.value })}
          required
        />
        <button type="submit">Finalizar Cadastro</button>
      </form>
    </div>
  );
};

export default Cadastro;