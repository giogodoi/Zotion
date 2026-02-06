import React, { useState, useEffect, useCallback } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import api from './services/api';

import Login from './pages/Login';
import Cadastro from './pages/Cadastro';
import Painel from './pages/Painel';
import Lembretes from './pages/Lembretes';
import Calendario from './pages/Calendario';
import Notas from './pages/Notas';

import type { Lembrete, Nota } from './types';

// Aqui implementei rotas privadas para garantir que, caso um usuário mapeasse as todas rotas do react,
// Não seja possível passar sem estar logado. 

const RotaPrivada = ({ children }: { children: React.ReactNode }) => {
  const token = localStorage.getItem('@Zotion:token');
  return token ? <>{children}</> : <Navigate to="/" replace />;
};

function App() {
  const [lembretes, setLembretes] = useState<Lembrete[]>([]);
  const [notas, setNotas] = useState<Nota[]>([]);

  const carregarDadosDoBanco = useCallback(async () => {
    try {
      const [resLembretes, resNotas] = await Promise.all([
        api.get('/lembretes'),
        api.get('/notas')
      ]);
      setLembretes(resLembretes.data);
      setNotas(resNotas.data);
    } catch (error) {
      console.error("Erro ao sincronizar dados:", error);
    }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('@Zotion:token');
    
    if (token) {
      const inicializarApp = async () => {
        await carregarDadosDoBanco();
      };
      inicializarApp();
    }
  }, [carregarDadosDoBanco]);

  const lidarComLogout = () => {
    localStorage.clear();
    window.location.href = '/';
  };

  return (
    <BrowserRouter>
      <Routes>
        {/* ROTAS PÚBLICAS, ou seja, acessáveis sem token :D --- */}
        <Route 
          path="/" 
          element={
            <Login 
              onLoginSuccess={() => window.location.href = '/painel'} 
              onGoToCadastro={() => window.location.href = '/cadastro'} 
            />
          } 
        />
        
        <Route path="/cadastro" element={<Cadastro />} />

        {/*ROTAS PRIVADAS:*/}
        <Route 
          path="/painel" 
          element={
            <RotaPrivada>
              <Painel onLogout={lidarComLogout} />
            </RotaPrivada>
          }
        >
          <Route 
            path="lembretes" 
            element={<Lembretes lembretes={lembretes} setLembretes={setLembretes} />} 
          />
          <Route 
            path="calendario" 
            element={<Calendario lembretes={lembretes} notas={notas} />} 
          />
          <Route 
            path="notas" 
            element={<Notas notas={notas} setNotas={setNotas} />} 
          />
          <Route index element={<Navigate to="notas" replace />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;