import React, { useState } from 'react';
import api from '../services/api';

interface LoginProps {
  onLoginSuccess: () => void;
  onGoToCadastro: () => void;
}

const Login: React.FC<LoginProps> = ({ onLoginSuccess, onGoToCadastro }) => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [carregando, setCarregando] = useState(false);

  const lidarComLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setCarregando(true);

    // Para evitar o erro que comentei no nosso .api, tentamos fazer a limpeza de todo o cache.
    localStorage.clear();

    try {
      // Preciso tomar cuidado com o localStorage, pois ele pode armazenar um accessToken antigo e causar erros...
      // Veremos durante os testes :D
      const { data } = await api.post('/autenticacao/login', { email, senha });

      localStorage.setItem('@Zotion:token', data.valorToken);
      localStorage.setItem('@Zotion:usuario', data.nome || 'Usuário');

      onLoginSuccess();
    } catch {
      alert("E-mail ou senha inválidos.");
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <header>
        <div className="logo"><img src="/logo.png" alt="Zotion" /></div>
        <h1>Olá, bem-vindo!</h1>
        <p>Novo por aqui? <span className="link-destaque" onClick={onGoToCadastro}><strong>Crie sua conta</strong></span></p>
      </header>
      <form onSubmit={lidarComLogin}>
        <input type="email" placeholder="E-mail" value={email} onChange={e => setEmail(e.target.value)} required />
        <input type="password" placeholder="Senha" value={senha} onChange={e => setSenha(e.target.value)} required />
        <button type="submit" disabled={carregando}>{carregando ? 'Entrando...' : 'Entrar'}</button>
      </form>
    </div>
  );
};

export default Login;