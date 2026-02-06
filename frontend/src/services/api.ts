import axios, { type InternalAxiosRequestConfig } from 'axios';

const api = axios.create({
  baseURL: '/api', 
});

// Essa parte serve para salvar o JWT Token do usuário para que consigamos fazer as demais requisições depois do login
// Importante lembrar que esse interceptador é muito útil, porém é necessário tomar cuidado para não salvar tokens antigos no cache
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('@Zotion:token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;