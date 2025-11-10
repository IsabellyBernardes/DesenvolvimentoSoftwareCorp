package ifpe.paokentyn.test;

import ifpe.paokentyn.domain.Funcionario;
import ifpe.paokentyn.domain.Padaria;
import ifpe.paokentyn.repository.FuncionarioRepositorio;
import ifpe.paokentyn.repository.PadariaRepositorio;

import java.util.Calendar;
import java.util.Date;

public class FuncionarioTest {
    
    // Repositórios assumidos
    private final static PadariaRepositorio padariaRepositorio = new PadariaRepositorio();
    private final static FuncionarioRepositorio funcionarioRepositorio = new FuncionarioRepositorio();
    
    // Variavel para armazenar a Padaria criada
    private static Padaria padariaBase;

    public static void main(String[] args) {
        // 1. Configuração inicial: garante que uma Padaria exista
        configurarPadariaBase(); 
        
        // 2. Executa os testes de CRUD
        if (padariaBase != null) {
            testarPersistencia(padariaBase);
            testarConsultaPorId(1L);
        } else {
            System.err.println("Erro: Não foi possível configurar a Padaria base para o teste.");
        }
    }

    // Cria e salva uma Padaria para ser usada como chave estrangeira.
   
    public static void configurarPadariaBase() {
        System.out.println("=== CONFIGURANDO PADARIA BASE (FK) ===");
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria do Zé");
        padaria.setCep("51020000");
        // Salvar Padaria
        padariaRepositorio.salvar(padaria);
        padariaBase = padaria;
        System.out.println("Padaria Base criada com ID: " + padariaBase.getId());
    }
    
    // Testa a persistência (salvamento) de um novo Funcionario.
    
    public static void testarPersistencia(Padaria padaria) {
        System.out.println("\n=== TESTE PERSISTÊNCIA FUNCIONÁRIO ===");
        
        // Data de contratação deve ser @Past
        Date dataContratacao = getData(10, Calendar.JANUARY, 2024);
        
        Funcionario funcionario = new Funcionario();
        funcionario.setPadaria(padaria); // FK obrigatória (@NotNull)
        funcionario.setNome("Ana Carolina da Silva"); // @NotBlank, @Size(max=255)
        funcionario.setCargo("Caixa Principal"); // @NotBlank, @Size(max=50)
        funcionario.setDataContratacao(dataContratacao); // @Past
        funcionario.setSalario(1850.75); // @DecimalMin(1000.00)
        
        funcionarioRepositorio.salvar(funcionario);
        System.out.println("Funcionário cadastrado com ID: " + funcionario.getId());
    }

    // Testa a consulta de um Funcionario pelo seu ID.
   
    public static void testarConsultaPorId(Long id) {
        System.out.println("\n=== TESTE CONSULTA POR ID FUNCIONÁRIO ===");
        
        Funcionario funcionario = funcionarioRepositorio.encontrarPorID(id);
        
        if (funcionario != null) {
            System.out.println("Funcionário encontrado (ID: " + funcionario.getId() + ")");
            System.out.println("Nome: " + funcionario.getNome());
            System.out.println("Cargo: " + funcionario.getCargo());
            // Acesso LAZY: o nome da Padaria é carregado neste momento
            System.out.println("Trabalha na Padaria: " + funcionario.getPadaria().getNome()); 
        } else {
            System.out.println("Funcionário com ID " + id + " não encontrado.");
        }
    }

    // --- Método Auxiliar de Data ---
    
    private static Date getData(int dia, int mes, int ano) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ano);
        calendar.set(Calendar.MONTH, mes);
        calendar.set(Calendar.DAY_OF_MONTH, dia);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
