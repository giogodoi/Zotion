// Esse arquivo declara os tipos glovais para o Typescript
// A minha ideia com isso é evitar repetição e ter que declarar essas tipagens em diversos componentes.

export interface Lembrete {
  id: string;
  nome: string;
  descricao: string;
  dataHora: string;
  prioridade: number;
  realizada: boolean;
}

export interface Nota {
  id: string;
  titulo: string;
  conteudo: string;
  cor: string;
  realizada: boolean;
  data?: string;
}