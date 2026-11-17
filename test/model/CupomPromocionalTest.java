package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CupomPromocionalTest {

    @Test
    @DisplayName("Deve aplicar corretamente o fator de desconto para valores positivos")
    void deveAplicarDescontoParaValoresPositivos() {
        assertEquals(50.0, CupomPromocional.FIDELIDADE.aplicarDesconto(100.0), 0.001);
        assertEquals(70.0, CupomPromocional.BRADESCO.aplicarDesconto(100.0), 0.001);
        assertEquals(80.0, CupomPromocional.ELO.aplicarDesconto(100.0), 0.001);
    }

    @Test
    @DisplayName("Deve retornar zero quando o valor bruto for zero ou negativo")
    void deveRetornarZeroParaValorZeroOuNegativo() {
        assertEquals(0.0, CupomPromocional.FIDELIDADE.aplicarDesconto(0.0), 0.001);
        assertEquals(0.0, CupomPromocional.BRADESCO.aplicarDesconto(-0.01), 0.001);
        assertEquals(0.0, CupomPromocional.ELO.aplicarDesconto(-150.0), 0.001);
    }

    @Test
    @DisplayName("Deve encontrar cupom ignorando espaços e diferença entre maiúsculas e minúsculas")
    void deveBuscarCupomValidoComNormalizacao() {
        assertEquals(CupomPromocional.FIDELIDADE, CupomPromocional.buscarPorCodigo("FIDELIDADE"));
        assertEquals(CupomPromocional.BRADESCO, CupomPromocional.buscarPorCodigo("  bradesco  "));
        assertEquals(CupomPromocional.ELO, CupomPromocional.buscarPorCodigo("eLo"));
    }

    @Test
    @DisplayName("Deve retornar null para códigos inexistentes, vazios ou nulos")
    void deveRetornarNullParaEntradasInvalidas() {
        assertNull(CupomPromocional.buscarPorCodigo("CUPOM_INEXISTENTE"));
        assertNull(CupomPromocional.buscarPorCodigo(""));
        assertNull(CupomPromocional.buscarPorCodigo("    "));
        assertNull(CupomPromocional.buscarPorCodigo(null));
    }
}