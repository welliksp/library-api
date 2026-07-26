package br.com.wsp.library.api.config;

import br.com.wsp.library.api.entity.LivroEntity;
import br.com.wsp.library.api.entity.enums.Genero;
import br.com.wsp.library.api.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final LivroRepository repository;

    @Override
    public void run(String... args) {
        try {
            long total = repository.count();
            log.info("Total de livros na base: {}", total);
            if (total > 0) {
                log.info("Base de dados já populada, seeder ignorado.");
                return;
            }

            List<LivroEntity> livros = livros();
            log.info("Iniciando inserção de {} livros...", livros.size());
            repository.saveAll(livros);
            log.info("150 livros inseridos com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao executar DataSeeder: {}", e.getMessage(), e);
        }
    }

    private List<LivroEntity> livros() {
        LocalDateTime agora = LocalDateTime.now();
        return List.of(
            livro("Dom Casmurro", "Machado de Assis", "9788520914646", 1899, Genero.ROMANCE, agora),
            livro("O Cortiço", "Aluísio Azevedo", "9788535902778", 1890, Genero.ROMANCE, agora),
            livro("Iracema", "José de Alencar", "9788520916534", 1865, Genero.ROMANCE, agora),
            livro("Memórias Póstumas de Brás Cubas", "Machado de Assis", "9788535910663", 1881, Genero.ROMANCE, agora),
            livro("A Moreninha", "Joaquim Manuel de Macedo", "9788572328745", 1844, Genero.ROMANCE, agora),
            livro("Capitães da Areia", "Jorge Amado", "9788535911329", 1937, Genero.ROMANCE, agora),
            livro("Gabriela, Cravo e Canela", "Jorge Amado", "9788535906929", 1958, Genero.ROMANCE, agora),
            livro("Grande Sertão: Veredas", "João Guimarães Rosa", "9788526807136", 1956, Genero.ROMANCE, agora),
            livro("Vidas Secas", "Graciliano Ramos", "9788535902785", 1938, Genero.ROMANCE, agora),
            livro("O Guarani", "José de Alencar", "9788520916541", 1857, Genero.ROMANCE, agora),
            livro("Dune", "Frank Herbert", "9780441013593", 1965, Genero.FICCAO_CIENTIFICA, agora),
            livro("Fundação", "Isaac Asimov", "9780553293357", 1951, Genero.FICCAO_CIENTIFICA, agora),
            livro("Neuromancer", "William Gibson", "9780441569595", 1984, Genero.FICCAO_CIENTIFICA, agora),
            livro("1984", "George Orwell", "9780451524935", 1949, Genero.FICCAO_CIENTIFICA, agora),
            livro("Admirável Mundo Novo", "Aldous Huxley", "9780060850524", 1932, Genero.FICCAO_CIENTIFICA, agora),
            livro("O Fim da Eternidade", "Isaac Asimov", "9780586024577", 1955, Genero.FICCAO_CIENTIFICA, agora),
            livro("Fahrenheit 451", "Ray Bradbury", "9781451673319", 1953, Genero.FICCAO_CIENTIFICA, agora),
            livro("O Homem do Castelo Alto", "Philip K. Dick", "9780547572482", 1962, Genero.FICCAO_CIENTIFICA, agora),
            livro("Eu, Robô", "Isaac Asimov", "9780553294385", 1950, Genero.FICCAO_CIENTIFICA, agora),
            livro("Contato", "Carl Sagan", "9780671434007", 1985, Genero.FICCAO_CIENTIFICA, agora),
            livro("O Senhor dos Anéis: A Sociedade do Anel", "J.R.R. Tolkien", "9780618640157", 1954, Genero.FANTASIA, agora),
            livro("O Senhor dos Anéis: As Duas Torres", "J.R.R. Tolkien", "9780618346257", 1954, Genero.FANTASIA, agora),
            livro("O Senhor dos Anéis: O Retorno do Rei", "J.R.R. Tolkien", "9780618346264", 1955, Genero.FANTASIA, agora),
            livro("O Hobbit", "J.R.R. Tolkien", "9780547928227", 1937, Genero.FANTASIA, agora),
            livro("Harry Potter e a Pedra Filosofal", "J.K. Rowling", "9788532511010", 1997, Genero.FANTASIA, agora),
            livro("Harry Potter e a Câmara Secreta", "J.K. Rowling", "9788532511027", 1998, Genero.FANTASIA, agora),
            livro("Harry Potter e o Prisioneiro de Azkaban", "J.K. Rowling", "9788532511034", 1999, Genero.FANTASIA, agora),
            livro("Harry Potter e o Cálice de Fogo", "J.K. Rowling", "9788532511041", 2000, Genero.FANTASIA, agora),
            livro("Harry Potter e a Ordem da Fênix", "J.K. Rowling", "9788532511058", 2003, Genero.FANTASIA, agora),
            livro("Harry Potter e o Enigma do Príncipe", "J.K. Rowling", "9788532511065", 2005, Genero.FANTASIA, agora),
            livro("It: A Coisa", "Stephen King", "9781501142970", 1986, Genero.TERROR, agora),
            livro("O Iluminado", "Stephen King", "9780307743657", 1977, Genero.TERROR, agora),
            livro("Cemitério Maldito", "Stephen King", "9781501156700", 1983, Genero.TERROR, agora),
            livro("Drácula", "Bram Stoker", "9780141439846", 1897, Genero.TERROR, agora),
            livro("Frankenstein", "Mary Shelley", "9780141439471", 1818, Genero.TERROR, agora),
            livro("O Exorcista", "William Peter Blatty", "9780061007224", 1971, Genero.TERROR, agora),
            livro("A Assombração da Casa da Colina", "Shirley Jackson", "9780143039983", 1959, Genero.TERROR, agora),
            livro("O Chamado de Cthulhu", "H.P. Lovecraft", "9780141182346", 1928, Genero.TERROR, agora),
            livro("Misery", "Stephen King", "9781501143106", 1987, Genero.TERROR, agora),
            livro("O Médico e o Monstro", "Robert Louis Stevenson", "9780141389509", 1886, Genero.TERROR, agora),
            livro("Steve Jobs", "Walter Isaacson", "9781451648539", 2011, Genero.BIOGRAFIA, agora),
            livro("Elon Musk", "Walter Isaacson", "9781982181284", 2023, Genero.BIOGRAFIA, agora),
            livro("Leonardo da Vinci", "Walter Isaacson", "9781501139154", 2017, Genero.BIOGRAFIA, agora),
            livro("A História de Pi", "Yann Martel", "9780156027328", 2001, Genero.BIOGRAFIA, agora),
            livro("O Diário de Anne Frank", "Anne Frank", "9780553296983", 1947, Genero.BIOGRAFIA, agora),
            livro("Longa Caminhada até a Liberdade", "Nelson Mandela", "9780316548182", 1994, Genero.BIOGRAFIA, agora),
            livro("A Autobiografia de Malcolm X", "Malcolm X", "9780345350688", 1965, Genero.BIOGRAFIA, agora),
            livro("Eu Sou Malala", "Malala Yousafzai", "9780316322409", 2013, Genero.BIOGRAFIA, agora),
            livro("Einstein: Sua Vida, Seu Universo", "Walter Isaacson", "9780743264747", 2007, Genero.BIOGRAFIA, agora),
            livro("A Vida de Churchill", "Andrew Roberts", "9780670025022", 2018, Genero.BIOGRAFIA, agora),
            livro("Sapiens: Uma Breve História da Humanidade", "Yuval Noah Harari", "9780062316097", 2011, Genero.HISTORIA, agora),
            livro("Homo Deus", "Yuval Noah Harari", "9780062464316", 2015, Genero.HISTORIA, agora),
            livro("21 Lições para o Século 21", "Yuval Noah Harari", "9780525512172", 2018, Genero.HISTORIA, agora),
            livro("Guns, Germs, and Steel", "Jared Diamond", "9780393317558", 1997, Genero.HISTORIA, agora),
            livro("A Arte da Guerra", "Sun Tzu", "9781599869773", 2006, Genero.HISTORIA, agora),
            livro("O Príncipe", "Nicolau Maquiavel", "9780140449150", 1532, Genero.HISTORIA, agora),
            livro("Civilização", "Niall Ferguson", "9781594203053", 2011, Genero.HISTORIA, agora),
            livro("O Mundo Assombrado pelos Demônios", "Carl Sagan", "9780345409461", 1995, Genero.HISTORIA, agora),
            livro("Uma Breve História do Tempo", "Stephen Hawking", "9780553380163", 1988, Genero.HISTORIA, agora),
            livro("O Gene: Uma História Íntima", "Siddhartha Mukherjee", "9781476733500", 2016, Genero.HISTORIA, agora),
            livro("Clean Code", "Robert C. Martin", "9780132350884", 2008, Genero.TECNOLOGIA, agora),
            livro("The Pragmatic Programmer", "Andrew Hunt", "9780135957059", 2019, Genero.TECNOLOGIA, agora),
            livro("Design Patterns", "Gang of Four", "9780201633610", 1994, Genero.TECNOLOGIA, agora),
            livro("Refactoring", "Martin Fowler", "9780134757599", 2018, Genero.TECNOLOGIA, agora),
            livro("Domain-Driven Design", "Eric Evans", "9780321125217", 2003, Genero.TECNOLOGIA, agora),
            livro("The Clean Coder", "Robert C. Martin", "9780137081073", 2011, Genero.TECNOLOGIA, agora),
            livro("Working Effectively with Legacy Code", "Michael Feathers", "9780131177055", 2004, Genero.TECNOLOGIA, agora),
            livro("Continuous Delivery", "Jez Humble", "9780321601919", 2010, Genero.TECNOLOGIA, agora),
            livro("Site Reliability Engineering", "Betsy Beyer", "9781491929124", 2016, Genero.TECNOLOGIA, agora),
            livro("The Phoenix Project", "Gene Kim", "9781942788294", 2013, Genero.TECNOLOGIA, agora),
            livro("Microservices Patterns", "Chris Richardson", "9781617294549", 2018, Genero.TECNOLOGIA, agora),
            livro("Building Microservices", "Sam Newman", "9781492034025", 2021, Genero.TECNOLOGIA, agora),
            livro("Kubernetes in Action", "Marko Luksa", "9781617293726", 2017, Genero.TECNOLOGIA, agora),
            livro("Spring in Action", "Craig Walls", "9781617294945", 2018, Genero.TECNOLOGIA, agora),
            livro("Java: The Complete Reference", "Herbert Schildt", "9781260440232", 2020, Genero.TECNOLOGIA, agora),
            livro("Effective Java", "Joshua Bloch", "9780134685991", 2018, Genero.TECNOLOGIA, agora),
            livro("Head First Design Patterns", "Eric Freeman", "9780596007126", 2004, Genero.TECNOLOGIA, agora),
            livro("Introduction to Algorithms", "Thomas H. Cormen", "9780262033848", 2009, Genero.TECNOLOGIA, agora),
            livro("The Art of Computer Programming", "Donald Knuth", "9780201896831", 1968, Genero.TECNOLOGIA, agora),
            livro("Structure and Interpretation of Computer Programs", "Harold Abelson", "9780262510875", 1996, Genero.TECNOLOGIA, agora),
            livro("O Pequeno Príncipe", "Antoine de Saint-Exupéry", "9788576570004", 1943, Genero.INFANTIL, agora),
            livro("Alice no País das Maravilhas", "Lewis Carroll", "9780141439761", 1865, Genero.INFANTIL, agora),
            livro("Pinóquio", "Carlo Collodi", "9780141441368", 1883, Genero.INFANTIL, agora),
            livro("Peter Pan", "J.M. Barrie", "9780141439907", 1911, Genero.INFANTIL, agora),
            livro("O Mágico de Oz", "L. Frank Baum", "9780141321028", 1900, Genero.INFANTIL, agora),
            livro("Matilda", "Roald Dahl", "9780142410370", 1988, Genero.INFANTIL, agora),
            livro("Charlie e a Fábrica de Chocolate", "Roald Dahl", "9780142410318", 1964, Genero.INFANTIL, agora),
            livro("James e o Pêssego Gigante", "Roald Dahl", "9780142410363", 1961, Genero.INFANTIL, agora),
            livro("O BFG", "Roald Dahl", "9780142410387", 1982, Genero.INFANTIL, agora),
            livro("As Bruxas", "Roald Dahl", "9780142410394", 1983, Genero.INFANTIL, agora),
            livro("A Culpa é das Estrelas", "John Green", "9780525478812", 2012, Genero.ROMANCE, agora),
            livro("O Ladrão de Raios", "Rick Riordan", "9780786838653", 2005, Genero.FANTASIA, agora),
            livro("O Mar de Monstros", "Rick Riordan", "9781423103349", 2006, Genero.FANTASIA, agora),
            livro("A Maldição do Titã", "Rick Riordan", "9781423101451", 2007, Genero.FANTASIA, agora),
            livro("A Batalha do Labirinto", "Rick Riordan", "9781423101468", 2008, Genero.FANTASIA, agora),
            livro("O Último Olimpiano", "Rick Riordan", "9781423101475", 2009, Genero.FANTASIA, agora),
            livro("Jogos Vorazes", "Suzanne Collins", "9780439023481", 2008, Genero.FICCAO_CIENTIFICA, agora),
            livro("Em Chamas", "Suzanne Collins", "9780439023498", 2009, Genero.FICCAO_CIENTIFICA, agora),
            livro("A Esperança", "Suzanne Collins", "9780439023511", 2010, Genero.FICCAO_CIENTIFICA, agora),
            livro("Divergente", "Veronica Roth", "9780062024022", 2011, Genero.FICCAO_CIENTIFICA, agora),
            livro("Insurgente", "Veronica Roth", "9780062024046", 2012, Genero.FICCAO_CIENTIFICA, agora),
            livro("Convergente", "Veronica Roth", "9780062024060", 2013, Genero.FICCAO_CIENTIFICA, agora),
            livro("O Código Da Vinci", "Dan Brown", "9780307474278", 2003, Genero.HISTORIA, agora),
            livro("Anjos e Demônios", "Dan Brown", "9780671027360", 2000, Genero.HISTORIA, agora),
            livro("Inferno", "Dan Brown", "9780385537858", 2013, Genero.HISTORIA, agora),
            livro("Origem", "Dan Brown", "9780385514231", 2017, Genero.HISTORIA, agora),
            livro("O Símbolo Perdido", "Dan Brown", "9780385504225", 2009, Genero.HISTORIA, agora),
            livro("A Menina que Roubava Livros", "Markus Zusak", "9780375842207", 2005, Genero.HISTORIA, agora),
            livro("O Alquimista", "Paulo Coelho", "9780062315007", 1988, Genero.ROMANCE, agora),
            livro("Brida", "Paulo Coelho", "9780061687082", 1990, Genero.ROMANCE, agora),
            livro("Veronika Decide Morrer", "Paulo Coelho", "9780061724749", 1998, Genero.ROMANCE, agora),
            livro("O Diário de um Banana", "Jeff Kinney", "9780810993136", 2007, Genero.INFANTIL, agora),
            livro("Diário de um Banana 2: Rodrick é o Cara", "Jeff Kinney", "9780810994737", 2008, Genero.INFANTIL, agora),
            livro("Diário de um Banana 3: Horas Mortas", "Jeff Kinney", "9780810983915", 2009, Genero.INFANTIL, agora),
            livro("Diário de um Banana 4: Dias de Cão", "Jeff Kinney", "9780810989627", 2009, Genero.INFANTIL, agora),
            livro("Diário de um Banana 5: A Verdade Nua e Crua", "Jeff Kinney", "9780810997905", 2010, Genero.INFANTIL, agora),
            livro("O Senhor das Moscas", "William Golding", "9780571191475", 1954, Genero.TERROR, agora),
            livro("A Metamorfose", "Franz Kafka", "9780553213690", 1915, Genero.TERROR, agora),
            livro("O Retrato de Dorian Gray", "Oscar Wilde", "9780141439570", 1890, Genero.TERROR, agora),
            livro("Rebecca", "Daphne du Maurier", "9780380730407", 1938, Genero.TERROR, agora),
            livro("A Volta do Parafuso", "Henry James", "9780141441351", 1898, Genero.TERROR, agora),
            livro("Elon Musk: Tesla, SpaceX e a Missão por um Futuro Fantástico", "Ashlee Vance", "9780062301239", 2015, Genero.BIOGRAFIA, agora),
            livro("A Origem das Espécies", "Charles Darwin", "9780140432053", 1859, Genero.HISTORIA, agora),
            livro("O Mundo de Sofia", "Jostein Gaarder", "9780374528256", 1991, Genero.HISTORIA, agora),
            livro("Cosmos", "Carl Sagan", "9780345539434", 1980, Genero.HISTORIA, agora),
            livro("Pálido Ponto Azul", "Carl Sagan", "9780345376596", 1994, Genero.HISTORIA, agora),
            livro("Clean Architecture", "Robert C. Martin", "9780134494166", 2017, Genero.TECNOLOGIA, agora),
            livro("Accelerate", "Nicole Forsgren", "9781942788331", 2018, Genero.TECNOLOGIA, agora),
            livro("The DevOps Handbook", "Gene Kim", "9781942788003", 2016, Genero.TECNOLOGIA, agora),
            livro("Designing Data-Intensive Applications", "Martin Kleppmann", "9781449373320", 2017, Genero.TECNOLOGIA, agora),
            livro("System Design Interview", "Alex Xu", "9798664653403", 2020, Genero.TECNOLOGIA, agora),
            livro("A Revolução dos Bichos", "George Orwell", "9780451526342", 1945, Genero.FICCAO_CIENTIFICA, agora),
            livro("O Conto da Aia", "Margaret Atwood", "9780385490818", 1985, Genero.FICCAO_CIENTIFICA, agora),
            livro("Solaris", "Stanislaw Lem", "9780156027601", 1961, Genero.FICCAO_CIENTIFICA, agora),
            livro("O Guia do Mochileiro das Galáxias", "Douglas Adams", "9780345391803", 1979, Genero.FICCAO_CIENTIFICA, agora),
            livro("Ender's Game", "Orson Scott Card", "9780812550702", 1985, Genero.FICCAO_CIENTIFICA, agora),
            livro("A Wheel of Time: The Eye of the World", "Robert Jordan", "9780765305343", 1990, Genero.FANTASIA, agora),
            livro("A Game of Thrones", "George R.R. Martin", "9780553593716", 1996, Genero.FANTASIA, agora),
            livro("A Clash of Kings", "George R.R. Martin", "9780553579901", 1998, Genero.FANTASIA, agora),
            livro("A Storm of Swords", "George R.R. Martin", "9780553573428", 2000, Genero.FANTASIA, agora),
            livro("Name of the Wind", "Patrick Rothfuss", "9780756404079", 2007, Genero.FANTASIA, agora),
            livro("The Way of Kings", "Brandon Sanderson", "9780765326355", 2010, Genero.FANTASIA, agora),
            livro("Mistborn: The Final Empire", "Brandon Sanderson", "9780765311788", 2006, Genero.FANTASIA, agora),
            livro("Eragon", "Christopher Paolini", "9780375826696", 2003, Genero.FANTASIA, agora)
        );
    }

    private LivroEntity livro(String titulo, String autor, String isbn, int ano, Genero genero, LocalDateTime agora) {
        return LivroEntity.builder()
                .titulo(titulo)
                .autor(autor)
                .isbn(isbn)
                .anoPublicacao(ano)
                .genero(genero)
                .disponivel(true)
                .dataInclusao(agora)
                .build();
    }
}
