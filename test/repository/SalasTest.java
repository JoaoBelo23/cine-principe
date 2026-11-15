package repository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SalasTest {

  @Test
  void RetornoMultCorretos() {
    assertEquals(1.0, Salas.SALACOMUM.getMultiplicadorValor());
    assertEquals(1.2, Salas.SALA3D.getMultiplicadorValor());
    assertEquals(1.3, Salas.SALAXD.getMultiplicadorValor());
    assertEquals(1.4, Salas.SALAXD3D.getMultiplicadorValor());
  }

  @Test
  void EncontrarSalaPorTipo() {
    assertSame(Salas.SALA3D, Salas.obterPorTipo("3D"));
    assertSame(Salas.SALACOMUM, Salas.obterPorTipo("COMUM"));
  }

  @Test
  void RejeitarTipoInexistente() {
    assertThrows(IllegalArgumentException.class, () -> Salas.obterPorTipo("4D"));
  }
}
