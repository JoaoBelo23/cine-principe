package service;

import model.Filme;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessaoTest {
  // metodo auxiliar para nao ter que ficar criando filme
  private Sessao criarSessao() {
    Filme filme = new Filme("Teste", "1h", "Terror", 20.0f, "medo.png");
    return new Sessao(filme, "20:00");
  }

  @Test
  void cadeiraCorretaA1() {
    Sessao sessao = criarSessao();

    boolean resultado = sessao.traducaoCadeira("A1");

    assertTrue(resultado);
    assertEquals(1, sessao.getCadeira()[0][0]);
  }

  @Test
  void limiteCadeiraJ15() {
    Sessao sessao = criarSessao();

    boolean resultado = sessao.traducaoCadeira("J15");
    assertTrue(resultado);
    assertEquals(1, sessao.getCadeira()[9][14]);
  }

  @Test
  void rejeitarColunaAcimaDoLimite() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.escolhaCadeira(0, 15));
  }

  @Test
  void rejeitarLinhaAcimaDoLimite() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.escolhaCadeira(10, 0));
  }

  @Test
  void rejeitarColunaNegativa() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.escolhaCadeira(0, -1));
  }

  @Test
  void rejeitarLinhaNegativa() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.escolhaCadeira(-1, 0));
  }

  @Test
  void retornoNomeFilme() {
    Sessao sessao = criarSessao();
    assertEquals("Teste", sessao.getNomeFilme());
  }

  @Test
  void retornoValorFilme() {
    Sessao sessao = criarSessao();
    assertEquals(20.0f, sessao.getValorFilme(), 0.001f);
  }

  @Test
  void rejeitarEntradaNula() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.traducaoCadeira(null));
  }

  @Test
  void rejeitarEntradaVazia() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.traducaoCadeira(""));
  }

  @Test
  void rejeitarColunaZero() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.traducaoCadeira("A0"));
  }

  @Test
  void rejeitarStringColunaAcimaDoLimite() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.traducaoCadeira("A16"));
  }

  @Test
  void rejeitarStringLinhaAcimaDoLimite() {
    Sessao sessao = criarSessao();
    assertFalse(sessao.traducaoCadeira("K1"));
  }

  @Test
  void limparCadeiraOcupada() {
    Sessao sessao = criarSessao();

    sessao.escolhaCadeira(0, 0);
    sessao.limparCadeira(0, 0);

    assertEquals(0, sessao.getCadeira()[0][0]);
  }

}
