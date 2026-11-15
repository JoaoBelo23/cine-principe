package service;

import model.Bilhete;
import model.Critico;
import model.CupomPromocional;
import model.DadosCheckout;
import model.Filme;
import model.Sala;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutFacadeTest {

    private FormatadorRecibo formatadorRecibo;
    private Usuario usuarioPadrao;
    private Critico usuarioCritico;

    @BeforeEach
    void setUp() {
        formatadorRecibo = new FormatadorRecibo();

        usuarioPadrao = new Usuario(
                "eduardo", "12345678900", "senha123", 22, "M",
                "eduardo@email.com", "EDUARDO A OLIVEIRA", "4111222233334444", "123"
        );

        usuarioCritico = new Critico(
                "critico_cine", "99988877766", "senha123", 35, "M",
                "critico@cinema.com", "CRITICO OFICIAL", "5555666677778888", "999", "Site"
        );
    }

    private Bilhete criarBilheteExemplo(Usuario comprador, String tipoSala, float valorFilme, String cadeira) {
        Filme filme = new Filme("Duna: Parte 2", "166", "Sinopse", valorFilme, "Imagem");
        Sessao sessao = new Sessao(filme, "19:30");
        Sala sala = new Sala(tipoSala, new Sessao[]{sessao});
        return new Bilhete(comprador, sala, 0, cadeira);
    }

    @Test
    @DisplayName("Deve processar checkout padrão sem cupom e sem exigir avaliação de crítico")
    void deveProcessarCheckoutSemCupom() {
        Bilhete bilhete = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "A1");
        CheckoutFacade facade = new CheckoutFacade(formatadorRecibo, (perfil, base) -> base);

        DadosCheckout dados = facade.processarCheckout(
                "Nenhum",
                List.of(bilhete),
                15.0,
                null
        );

        assertNotNull(dados);
        assertEquals(usuarioPadrao, dados.getUsuarioFinal());
        assertEquals(30.0, dados.getValorTotalBilhetes(), 0.01);
        assertEquals(45.0, dados.getValorFinal(), 0.01);
        assertFalse(dados.exigeCritica());
        assertNull(dados.getCritico());
    }

    @Test
    @DisplayName("Deve aplicar desconto do cupom promocional sobre bilhetes e produtos")
    void deveProcessarCheckoutComCupomPromocional() {
        Bilhete b1 = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "B1");
        Bilhete b2 = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "B2");
        CheckoutFacade facade = new CheckoutFacade(formatadorRecibo, (perfil, base) -> base);

        // Bilhetes = 60.0 | Produtos = 20.0 | Bruto = 80.0 | Cupom ELO (0.8) = 64.0
        DadosCheckout dados = facade.processarCheckout(
                "Nenhum",
                List.of(b1, b2),
                20.0,
                CupomPromocional.ELO
        );

        assertEquals(60.0, dados.getValorTotalBilhetes(), 0.01);
        assertEquals(64.0, dados.getValorFinal(), 0.01);
    }

    @Test
    @DisplayName("Deve sinalizar exigência de avaliação quando comprador for Crítico")
    void deveIdentificarCriticoParaAvaliacaoPosCompra() {
        Bilhete bilhete = criarBilheteExemplo(usuarioCritico, "IMAX", 40.0f, "C5");
        CheckoutFacade facade = new CheckoutFacade(formatadorRecibo, (perfil, base) -> usuarioCritico);

        DadosCheckout dados = facade.processarCheckout(
                "Crítico",
                List.of(bilhete),
                0.0,
                null
        );

        assertTrue(dados.exigeCritica());
        assertEquals(usuarioCritico, dados.getCritico());
        assertNotNull(dados.getFilme());
        assertEquals("Duna: Parte 2", dados.getFilme().getNome());
    }

    @Test
    @DisplayName("Deve gerar recibo formatado com os 4 últimos dígitos do cartão mascarados")
    void deveGerarReciboComCartaoMascarado() {
        Bilhete bilhete = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "D10");
        CheckoutFacade facade = new CheckoutFacade(formatadorRecibo, (perfil, base) -> base);

        DadosCheckout dados = facade.processarCheckout("Nenhum", List.of(bilhete), 0.0, null);
        String recibo = facade.gerarReciboCompra(dados, List.of(bilhete));

        assertTrue(recibo.contains("Compra realizada com sucesso!"));
        assertTrue(recibo.contains("********4444"));
        assertTrue(recibo.contains("Bilhete #1"));
        assertTrue(recibo.contains("Usuário: eduardo"));
        assertTrue(recibo.contains("Cadeira: D10"));
    }

    @Test
    @DisplayName("Deve mascarar cartão com asteriscos padrão quando número for inválido ou curto")
    void deveMascararCartaoInvalidoComPadrao() {
        Usuario usuarioSemCartao = new Usuario(
                "visitante", "11122233344", "123", 20, "F",
                "vis@email.com", "VISITANTE", "12", "10"
        );
        Bilhete bilhete = criarBilheteExemplo(usuarioSemCartao, "COMUM", 25.0f, "A2");
        CheckoutFacade facade = new CheckoutFacade(formatadorRecibo, (perfil, base) -> base);

        DadosCheckout dados = facade.processarCheckout("Nenhum", List.of(bilhete), 0.0, null);
        String recibo = facade.gerarReciboCompra(dados, List.of(bilhete));

        assertTrue(recibo.contains("Cobrado no cartão de final: ****"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando lista de bilhetes for vazia ou nula")
    void deveRejeitarListaBilhetesVaziaOuNula() {
        CheckoutFacade facade = new CheckoutFacade(formatadorRecibo, (perfil, base) -> base);

        assertThrows(IllegalArgumentException.class, () ->
                facade.processarCheckout("Nenhum", Collections.emptyList(), 10.0, null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                facade.processarCheckout("Nenhum", null, 10.0, null)
        );
    }
}