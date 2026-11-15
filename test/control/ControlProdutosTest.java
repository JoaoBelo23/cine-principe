package control;

import model.Produtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControlProdutosTest {

    private ControlProdutos controlProdutos;
    private Produtos[] catalogo;

    @BeforeEach
    void setUp() {
        controlProdutos = new ControlProdutos();
        catalogo = Produtos.values();
    }

    @Test
    @DisplayName("Deve iniciar com subtotal zerado e mapa de itens vazio")
    void deveIniciarComValoresZerados() {
        assertEquals(0.0, controlProdutos.getValorTotalProdutos(), 0.001);
        assertNotNull(controlProdutos.getItensSelecionados());
        assertTrue(controlProdutos.getItensSelecionados().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar zero quando todas as quantidades selecionadas forem zero")
    void deveCalcularSubtotalZeroQuandoNadaSelecionado() {
        int[] quantidadesZeradas = new int[catalogo.length];

        double subtotal = controlProdutos.calcularSubtotal(quantidadesZeradas);

        assertEquals(0.0, subtotal, 0.001);
        assertEquals(0.0, controlProdutos.getValorTotalProdutos(), 0.001);
    }

    @Test
    @DisplayName("Deve calcular corretamente o subtotal para múltiplos itens da bomboniere")
    void deveCalcularSubtotalParaMultiplosProdutos() {
        int[] quantidades = new int[catalogo.length];
        double totalEsperado = 0.0;

        for (int i = 0; i < catalogo.length; i++) {
            quantidades[i] = i + 1;
            totalEsperado += quantidades[i] * catalogo[i].getPreco();
        }

        double subtotalCalculado = controlProdutos.calcularSubtotal(quantidades);

        assertEquals(totalEsperado, subtotalCalculado, 0.001);
        assertEquals(totalEsperado, controlProdutos.getValorTotalProdutos(), 0.001);
    }

    @Test
    @DisplayName("Deve sobrescrever o subtotal anterior ao recalcular com novas quantidades")
    void deveSobrescreverSubtotalAnteriorAoRecalcular() {
        int[] primeiraSelecao = new int[catalogo.length];
        primeiraSelecao[0] = 3;
        controlProdutos.calcularSubtotal(primeiraSelecao);

        int[] segundaSelecao = new int[catalogo.length];
        segundaSelecao[0] = 1;
        double novoSubtotal = controlProdutos.calcularSubtotal(segundaSelecao);

        assertEquals(catalogo[0].getPreco(), novoSubtotal, 0.001);
        assertEquals(catalogo[0].getPreco(), controlProdutos.getValorTotalProdutos(), 0.001);
    }

    @Test
    @DisplayName("Deve registrar no mapa apenas produtos com quantidade maior que zero")
    void deveProcessarSelecaoIgnorandoItensComQuantidadeZero() {
        int[] quantidades = new int[catalogo.length];
        quantidades[0] = 2;
        if (catalogo.length > 2) {
            quantidades[2] = 4;
        }

        controlProdutos.processarSelecao(quantidades);
        Map<Produtos, Integer> selecionados = controlProdutos.getItensSelecionados();

        assertEquals(2, selecionados.get(catalogo[0]));
        if (catalogo.length > 1) {
            assertFalse(selecionados.containsKey(catalogo[1]));
        }
        if (catalogo.length > 2) {
            assertEquals(4, selecionados.get(catalogo[2]));
        }
    }

    @Test
    @DisplayName("Deve limpar seleção anterior ao processar nova escolha de produtos")
    void deveLimparSelecaoAnteriorAoProcessarNovaSelecao() {
        int[] primeiraSelecao = new int[catalogo.length];
        primeiraSelecao[0] = 2;
        controlProdutos.processarSelecao(primeiraSelecao);

        int[] segundaSelecao = new int[catalogo.length];
        if (catalogo.length > 1) {
            segundaSelecao[1] = 1;
        }
        controlProdutos.processarSelecao(segundaSelecao);

        Map<Produtos, Integer> selecionados = controlProdutos.getItensSelecionados();
        assertFalse(selecionados.containsKey(catalogo[0]));
        if (catalogo.length > 1) {
            assertEquals(1, selecionados.size());
            assertEquals(1, selecionados.get(catalogo[1]));
        }
    }
}