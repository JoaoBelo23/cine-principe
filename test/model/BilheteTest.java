package model;

import org.junit.jupiter.api.Test;
import service.Sessao;

import static org.junit.jupiter.api.Assertions.*;

class BilheteTest {

  private Bilhete criarBilhete(String tipoSala) {
    Filme filme = new Filme("Teste", "1h", "Terror", 20.0f, "medo.png");

    Sessao sessao = new Sessao(filme, "20:00");
    Sessao[] sessoes = { sessao };

    Sala sala = new Sala(tipoSala, sessoes);

    Usuario usuario = new Usuario("usuarioTeste", "12345678900", "123", 20, "Masc", "teste@email.com",
        "Cartao Teste", "1324123412341254", "123");

    return new Bilhete(usuario, sala, 0, "A1");
  }

  @Test
  void RetornarHorarioSessao() {
    Bilhete bilhete = criarBilhete("COMUM");

    assertEquals("20:00", bilhete.getHorario());
  }

  @Test
  void RetornarValorFilme() {
    Bilhete bilhete = criarBilhete("COMUM");

    assertEquals(20.0f, bilhete.getValor(), 0.001f);
  }

  @Test
  void RetornarNomeFilme() {
    Bilhete bilhete = criarBilhete("COMUM");
    assertEquals("Teste", bilhete.getNomeDoFilme());
  }

  @Test
  void CalcularValorComTaxaSalaComum() {
    Bilhete bilhete = criarBilhete("COMUM");

    assertEquals(20.0, bilhete.getValorComTaxaSala(), 0.001);
  }

  @Test
  void CalcularValorComTaxaSala3D() {
    Bilhete bilhete = criarBilhete("3D");

    assertEquals(24.0, bilhete.getValorComTaxaSala(), 0.001);
  }

  @Test
  void CalcularValorComTaxaSalaIMAX() {
    Bilhete bilhete = criarBilhete("IMAX");

    assertEquals(26.0, bilhete.getValorComTaxaSala(), 0.001);
  }

  @Test
  void CalcularValorComTaxaSalaIMAX3D() {
    Bilhete bilhete = criarBilhete("IMAX/3D");

    assertEquals(28.0, bilhete.getValorComTaxaSala(), 0.001);
  }
}
