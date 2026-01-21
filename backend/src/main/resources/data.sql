-- Garanta que os nomes das colunas coincidam com a Entity (nome em vez de name)
INSERT INTO perfis (perfil_id, nome) 
VALUES (gen_random_uuid(), 'ROLE_USER') 
ON CONFLICT (nome) DO NOTHING;