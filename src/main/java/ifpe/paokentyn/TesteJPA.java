package ifpe.paokentyn;

import ifpe.paokentyn.domain.Padaria;
import ifpe.paokentyn.repository.PadariaRepositorio;

public class TesteJPA {

    public static void main(String[] args) {
        PadariaRepositorio repo = new PadariaRepositorio();

        Padaria p = new Padaria();
        p.setNome("Padaria do Zé");
        p.setCep("54321000");

        repo.salvar(p);
        System.out.println("Padaria salva com ID: " + p.getId());
        
    }
}