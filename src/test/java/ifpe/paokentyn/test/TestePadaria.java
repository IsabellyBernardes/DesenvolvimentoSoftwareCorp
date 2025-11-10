package ifpe.paokentyn.test;

import ifpe.paokentyn.domain.Padaria;
import ifpe.paokentyn.repository.PadariaRepositorio;

public class TestePadaria {
    private final static PadariaRepositorio repositorio = new PadariaRepositorio();
    
    public static void main(String[] args) {
        testarPersistencia();
        testarConsultaPorId(1L);
    }
    
    public static void testarPersistencia() {
        System.out.println("=== TESTE PERSISTÊNCIA PADARIA ===");
        
        Padaria padaria = new Padaria();
        padaria.setNome("Padaria Pão Rei");
        padaria.setEndereco("Rua das Flores, 123");
        padaria.setCep("53431335");
        
        repositorio.salvar(padaria);
        System.out.println("Padaria cadastrada com ID: " + padaria.getId());
    }
    
     public static void testarConsultaPorId(Long id) {
        System.out.println("\n=== TESTE CONSULTA POR ID PADARIA ===");
        
        Padaria padaria = repositorio.encontrarPorID(id);
        System.out.println("Padaria encontrada: " + padaria.getNome());
        System.out.println("Endereço: " + padaria.getEndereco());
    }
}