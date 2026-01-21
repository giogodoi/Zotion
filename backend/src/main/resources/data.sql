# Precisamos criar uma role USER caso ela não exista para evitar erros na inicialização da aplicação.

INSERT INTO roles (role_id, name) 
VALUES (gen_random_uuid(), 'USER') 
ON CONFLICT (name) DO NOTHING;
