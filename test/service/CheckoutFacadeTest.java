package service;

import model.Bilhete;
import model.Critico;
import model.CupomPromocional;
import model.DadosCheckout;
import model.Estudante;
import model.Filme;
import model.Sala;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutFacadeTest {

    private FormatadorRecibo formatadorRecibo;
    private Usuario usuarioPadrao;
    private Critico usuarioCritico;
    private CheckoutFacade facade;

    @BeforeEach
    void setUp() {
        formatadorRecibo = new FormatadorRecibo();

        usuarioPadrao = new Usuario(
                "usuarioTeste", "12345678900", "123", 20, "Masc",
                "teste@email.com", "Cartao Teste", "1324123412344444", "123"
        );
        usuarioPadrao.setId(1);

        usuarioCritico = new Critico(
                "critico_cine", "99988877766", "senha123", 35, "M",
                "critico@cinema.com", "CRITICO OFICIAL", "5555666677778888", "999", "Omelete"
        );
        usuarioCritico.setId(2);

        ConsultorCritico consultorEmMemoria = username ->
                "critico_cine".equals(username) ? usuarioCritico : null;

        UsuarioPerfilFactory perfilFactory = new UsuarioPerfilFactory(consultorEmMemoria);
        facade = new CheckoutFacade(formatadorRecibo, perfilFactory::criar);
    }

    private Bilhete criarBilheteExemplo(Usuario comprador, String tipoSala, float valorFilme, String cadeira) {
        Filme filme = new Filme("Teste", "1h", "Terror", valorFilme, "medo.png");
        Sessao sessao = new Sessao(filme, "19:30");
        Sala sala = new Sala(tipoSala, new Sessao[]{sessao});
        return new Bilhete(comprador, sala, 0, cadeira);
    }

    @Test
    @DisplayName("Deve processar checkout com perfil Nenhum cobrando valor integral e mantendo ID do usuário")
    void deveProcessarCheckoutPerfilNenhumSemCupom() {
        Bilhete bilhete = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "A1");

        DadosCheckout dados = facade.processarCheckout(
                "Nenhum",
                List.of(bilhete),
                15.0,
                null
        );

        assertNotNull(dados);
        assertEquals(1, dados.getUsuarioFinal().getId());
        assertEquals("usuarioTeste", dados.getUsuarioFinal().getUser());
        assertEquals(30.0, dados.getValorTotalBilhetes(), 0.01);
        assertEquals(45.0, dados.getValorFinal(), 0.01);
        assertFalse(dados.exigeCritica());
        assertNull(dados.getCritico());
    }

    @Test
    @DisplayName("Deve criar Estudante pela fábrica e aplicar meia-entrada nos bilhetes")
    void deveProcessarCheckoutPerfilEstudanteComMeiaEntrada() {
        Bilhete b1 = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "A1");
        Bilhete b2 = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "A2");

        // 2 bilhetes de 30.0 com meia-entrada = 30.0 + 10.0 produtos = 40.0
        DadosCheckout dados = facade.processarCheckout(
                "Estudante",
                List.of(b1, b2),
                10.0,
                null
        );

        assertTrue(dados.getUsuarioFinal() instanceof Estudante);
        assertEquals(1, dados.getUsuarioFinal().getId());
        assertEquals(30.0, dados.getValorTotalBilhetes(), 0.01);
        assertEquals(40.0, dados.getValorFinal(), 0.01);
        assertFalse(dados.exigeCritica());
    }

    @Test
    @DisplayName("Deve resolver Crítico cadastrado pela fábrica, isentar 2 ingressos e exigir avaliação")
    void deveIdentificarCriticoCadastradoEIsentarDoisIngressos() {
        Bilhete b1 = criarBilheteExemplo(usuarioCritico, "COMUM", 40.0f, "C1");
        Bilhete b2 = criarBilheteExemplo(usuarioCritico, "COMUM", 40.0f, "C2");
        Bilhete b3 = criarBilheteExemplo(usuarioCritico, "COMUM", 40.0f, "C3");

        // 3 bilhetes de 40.0 para Crítico: isenta 2 e cobra 1 (40.0)
        DadosCheckout dados = facade.processarCheckout(
                "Crítico",
                List.of(b1, b2, b3),
                0.0,
                null
        );

        assertTrue(dados.exigeCritica());
        assertEquals(usuarioCritico, dados.getCritico());
        assertEquals(40.0, dados.getValorTotalBilhetes(), 0.01);
        assertNotNull(dados.getFilme());
        assertEquals("Teste", dados.getFilme().getNome());
    }

    @Test
    @DisplayName("Deve aplicar fallback para Usuário padrão quando perfil Crítico não estiver cadastrado")
    void deveAplicarPrecoPadraoQuandoPerfilCriticoNaoEstiverCadastrado() {
        Bilhete bilhete = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "B4");

        DadosCheckout dados = facade.processarCheckout(
                "Crítico",
                List.of(bilhete),
                0.0,
                null
        );

        assertFalse(dados.exigeCritica());
        assertNull(dados.getCritico());
        assertEquals(30.0, dados.getValorTotalBilhetes(), 0.01);
        assertEquals(30.0, dados.getValorFinal(), 0.01);
    }

    @Test
    @DisplayName("Deve aplicar desconto do cupom promocional sobre bilhetes e produtos")
    void deveProcessarCheckoutComCupomPromocional() {
        Bilhete b1 = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "B1");
        Bilhete b2 = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "B2");

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
    @DisplayName("Deve gerar recibo formatado com os 4 últimos dígitos do cartão mascarados")
    void deveGerarReciboComCartaoMascarado() {
        Bilhete bilhete = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "D10");

        DadosCheckout dados = facade.processarCheckout("Nenhum", List.of(bilhete), 0.0, null);
        String recibo = facade.gerarReciboCompra(dados, List.of(bilhete));

        assertTrue(recibo.contains("Compra realizada com sucesso!"));
        assertTrue(recibo.contains("********4444"));
        assertTrue(recibo.contains("Bilhete #1"));
        assertTrue(recibo.contains("Usuário: usuarioTeste"));
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

        DadosCheckout dados = facade.processarCheckout("Nenhum", List.of(bilhete), 0.0, null);
        String recibo = facade.gerarReciboCompra(dados, List.of(bilhete));

        assertTrue(recibo.contains("Cobrado no cartão de final: ****"));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando lista de bilhetes for vazia ou nula")
    void deveRejeitarListaBilhetesVaziaOuNula() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.processarCheckout("Nenhum", Collections.emptyList(), 10.0, null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                facade.processarCheckout("Nenhum", null, 10.0, null)
        );
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando a lista contiver bilhete nulo")
    void deveRejeitarListaContendoBilheteNulo() {
        Bilhete bilheteValido = criarBilheteExemplo(usuarioPadrao, "COMUM", 30.0f, "A1");

        assertThrows(IllegalArgumentException.class, () ->
                facade.processarCheckout("Nenhum", Collections.singletonList(null), 10.0, null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                facade.processarCheckout("Nenhum", Arrays.asList(bilheteValido, null), 10.0, null)
        );
    }
}