package ifpe.paokentyn.test;

import ifpe.paokentyn.domain.Pedido;
import ifpe.paokentyn.domain.ItemPedido;
import ifpe.paokentyn.domain.Pao;
import ifpe.paokentyn.repository.PedidoRepositorio;
import ifpe.paokentyn.repository.RepositorioGenerico;
import ifpe.paokentyn.repository.Repositorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestePedido {

    private final static PedidoRepositorio pedidoRepositorio = new PedidoRepositorio();
    private final static Repositorio<Pao> paoRepositorio = new RepositorioGenerico<>(Pao.class) {};

    public static void main(String[] args) {
        testarPersistencia();
        testarConsultaPorId(1L);
    }

    public static void testarPersistencia() {
        System.out.println("=== TESTE PERSISTÊNCIA PEDIDO ===");

        // 🔹 1. Cria e salva um pão (caso não exista)
        Pao pao = new Pao();
        pao.setNomePao("Pão Francês");
        pao.setPreco(1.50);
        paoRepositorio.salvar(pao);
        System.out.println("Pão salvo com ID: " + pao.getId());

        // 🔹 2. Cria um pedido
        Pedido pedido = new Pedido();
        pedido.setDataPedido(new Date());

        // 🔹 3. Cria os itens do pedido
        List<ItemPedido> itens = new ArrayList<>();

        ItemPedido item1 = new ItemPedido();
        item1.setPao(pao);
        item1.setQuantidade(10);
        item1.setPedido(pedido);

        ItemPedido item2 = new ItemPedido();
        item2.setPao(pao);
        item2.setQuantidade(5);
        item2.setPedido(pedido);

        itens.add(item1);
        itens.add(item2);

        pedido.setItens(itens);

        // 🔹 4. Calcula total com base no preço do pão
        double total = 0.0;
        for (ItemPedido item : itens) {
            total += item.getQuantidade() * item.getPao().getPreco();
        }
        pedido.setValorTotal(total);

        // 🔹 5. Salva o pedido
        pedidoRepositorio.salvar(pedido);

        System.out.println("Pedido salvo com ID: " + pedido.getId());
        System.out.println("Valor total do pedido: R$ " + pedido.getValorTotal());
    }

    public static void testarConsultaPorId(Long id) {
        System.out.println("\n=== TESTE CONSULTA POR ID PEDIDO ===");

        Pedido pedido = pedidoRepositorio.encontrarPorID(id);
        if (pedido != null) {
            System.out.println("Pedido encontrado!");
            System.out.println("Data do pedido: " + pedido.getDataPedido());
            System.out.println("Valor total: R$ " + pedido.getValorTotal());

            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                System.out.println("Itens do pedido:");
                for (ItemPedido item : pedido.getItens()) {
                    Pao pao = item.getPao();
                    System.out.println("- " + pao.getNomePao() + " (" + item.getQuantidade() + "x R$" + pao.getPreco() + ")");
                }
            } else {
                System.out.println("Nenhum item vinculado ao pedido.");
            }

        } else {
            System.out.println("Nenhum pedido encontrado com o ID informado.");
        }
    }
}
