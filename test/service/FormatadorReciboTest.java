package service;

import model.Bilhete;
import model.Filme;
import model.Sala;
import model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FormatadorReciboTest {

    private FormatadorRecibo formatador;
    private Usuario usuario;
    private Sala sala;

    @BeforeEach
    void setUp() {
        formatador = new FormatadorRecibo();
        usuario = new Usuario("usuarioTeste", "12345678900", "123", 20, "Masc", "teste@email.com",
        "Cartao Teste", "1324123412344444", "123");
        Filme filme = new Filme("Teste", "1h", "Terror", 20.0f, "medo.png");
        Sessao sessao = new Sessao(filme, "20:00");
        sala = new Sala("IMAX", new Sessao[]{sessao});
    }

    @Test
    @DisplayName("Deve mascarar cartão válido mantendo apenas os 4 últimos dígitos")
    void deveMascararCartaoValido() {
        assertEquals("********4444", formatador.mascararCartao("4111222233334444"));
        assertEquals("********1234", formatador.mascararCartao("  1234  "));
    }

    @Test
    @DisplayName("Deve retornar máscara padrão para cartão nulo, vazio ou com menos de 4 caracteres")
    void deveMascararCartaoInvalidoOuCurto() {
        assertEquals("****", formatador.mascararCartao(null));
        assertEquals("****", formatador.mascararCartao(""));
        assertEquals("****", formatador.mascararCartao("   "));
        assertEquals("****", formatador.mascararCartao("123"));
    }

    @Test
    @DisplayName("Deve ignorar bilhetes nulos na lista e manter numeração sequencial dos válidos")
    void deveIgnorarElementosNulosNaListaDeBilhetes() {
        Bilhete b1 = new Bilhete(usuario, sala, 0, "A1");
        Bilhete b2 = new Bilhete(usuario, sala, 0, "A2");

        String recibo = formatador.gerarRecibo(usuario, Arrays.asList(null, b1, null, b2, null));

        assertTrue(recibo.contains("Bilhete #1"));
        assertTrue(recibo.contains("Cadeira: A1"));
        assertTrue(recibo.contains("Bilhete #2"));
        assertTrue(recibo.contains("Cadeira: A2"));
        assertFalse(recibo.contains("Bilhete #3"));
    }

    @Test
    @DisplayName("Deve gerar cabeçalho sem falhar quando usuário ou lista de bilhetes forem nulos ou vazios")
    void deveTratarUsuarioOuListaNulaOuVazia() {
        String reciboListaNula = formatador.gerarRecibo(null, null);
        assertTrue(reciboListaNula.contains("Compra realizada com sucesso!"));
        assertTrue(reciboListaNula.contains("Cobrado no cartão de final: ****"));
        assertFalse(reciboListaNula.contains("Bilhete #1"));

        String reciboListaVazia = formatador.gerarRecibo(usuario, Collections.emptyList());
        assertTrue(reciboListaVazia.contains("********4444"));
        assertFalse(reciboListaVazia.contains("Bilhete #1"));
    }
}