package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import model.Bilhete;
import model.Filme;
import model.Sala;
import model.Usuario;

public class PrecificacaoStrategyTest {

    private ArrayList<Bilhete> criarBilhetes(String tipoSala, int quantidade) {

        Usuario usuario = new Usuario(
            "teste", "123", "123", 20, "M",
            "teste@email.com", "Teste", "123456", "123"
        );

        Filme filme = new Filme(
            "Filme Teste", "2h", "Drama", 20.0f, "imagem.png"
        );

        Sessao[] sessoes = new Sessao[7];
        sessoes[0] = new Sessao(filme, "18:00");

        Sala sala = new Sala(tipoSala, sessoes);

        ArrayList<Bilhete> bilhetes = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            Bilhete bilhete = new Bilhete(usuario, sala, 0, "A" + (i + 1));
            bilhetes.add(bilhete);
        }

        return bilhetes;
    }

    @Test
    public void testarPrecoPadrao() {

        PrecoPadraoStrategy estrategia = new PrecoPadraoStrategy();

        ArrayList<Bilhete> bilhetes = criarBilhetes("COMUM", 3);

        double resultado = estrategia.calcularPreco(bilhetes);

        assertEquals(60.0, resultado, 0.001);
    }

    @Test
    public void testarPrecoEstudante() {

        PrecoEstudanteStrategy estrategia = new PrecoEstudanteStrategy();

        ArrayList<Bilhete> bilhetes = criarBilhetes("COMUM", 3);

        double resultado = estrategia.calcularPreco(bilhetes);

        assertEquals(30.0, resultado, 0.001);
    }
}
