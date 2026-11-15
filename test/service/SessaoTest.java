package service; 

import model.Filme;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SesssaoTest{
  // metodo auxiliar para nao ter que ficar criando sessao    
  private Sessao criarSessao(){
    Filme filme = new Filme("Teste", "1h", "Terro", 20.0f, "medo.png");
    return new Sessao(filme, "20:00");
  }
  @Test
  void cadeiraCorretaA1(){
    Sessao sessao = criarSessao();
    
    boolean resultado = sessao.traducaoCadeira("A1");
    
    assertTrue(resultado);
    assertEquals(1, sessao.getCadeira()[0][0]);
  }

  @Test 
  void limiteCadeiraJ15(){
    Sessao sessao = criarSessao();

    boolean resultado = sessao.traducaoCadeira("J15");
    assertTrue(resultado);
    assertEquals(1, sessao.getCadeira()[9][14]);
  }
}
