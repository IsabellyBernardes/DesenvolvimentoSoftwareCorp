package ifpe.paokentyn.repository;

import java.util.List;

public interface Repositorio<T> {

    void salvar(T entity);
    T encontrarPorID(Long id);
    List<T> buscarTodos();
    void atualizar(T entity);
    void deletar(Long id);
}
