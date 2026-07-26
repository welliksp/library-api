db = db.getSiblingDB('library_db');

db.createUser({
    user: "library_user",
    pwd: "library_pass",
    roles: [
        {
            role: "readWrite",
            db: "library_db"
        }
    ]
});

db.createCollection('livros');

db.livros.createIndex({ isbn: 1 }, { unique: true });
db.livros.createIndex({ titulo: 'text', autor: 'text' });
db.livros.createIndex({ genero: 1 });
db.livros.createIndex({ disponivel: 1 });

print('MongoDB inicializado com sucesso!');
