package ifpe.paokentyn.test;

import ifpe.paokentyn.domain.Funcionario;
import ifpe.paokentyn.domain.Tarefa;
import ifpe.paokentyn.repository.TarefaRepositorio;

import java.util.Date;
import java.util.Calendar;

public class TesteTarefa {

    private final static TarefaRepositorio repositorio = new TarefaRepositorio();

    public static void main(String[] args) {
        testarPersistencia();
        testarConsultaPorId(1L);
    }

    public static void testarPersistencia() {
        System.out.println("=== TESTE PERSISTÊNCIA TAREFA ===");

        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);

        Tarefa tarefa = new Tarefa();
        tarefa.setFuncionario(funcionario);
        tarefa.setDescricao("Implementar módulo de autenticação");
        tarefa.setDataInicio(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        tarefa.setDataPrevisao(cal.getTime());

        tarefa.setConcluida(false);

        repositorio.salvar(tarefa);

        System.out.println("Tarefa cadastrada com ID: " + tarefa.getId());
    }

    public static void testarConsultaPorId(Long id) {
        System.out.println("\n=== TESTE CONSULTA POR ID TAREFA ===");

        Tarefa tarefa = repositorio.encontrarPorID(id);
        if (tarefa != null) {
            System.out.println("Tarefa encontrada: " + tarefa.getDescricao());
            System.out.println("Previsão: " + tarefa.getDataPrevisao());
            System.out.println("Concluída: " + tarefa.getConcluida());
        } else {
            System.out.println("Nenhuma tarefa encontrada com o ID informado.");
        }
    }
}
