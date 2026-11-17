package service;

import model.Bilhete;
import model.Usuario;
import java.util.List;

public class FormatadorRecibo {

    public String mascararCartao(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.trim().length() < 4) {
            return "****";
        }
        String limpo = numeroCartao.trim();
        return "********" + limpo.substring(limpo.length() - 4);
    }

    public String gerarRecibo(Usuario usuarioFinal, List<Bilhete> bilhetes) {
        String cartaoMascarado = usuarioFinal != null
                ? mascararCartao(usuarioFinal.getNumero_do_cartao())
                : "****";

        StringBuilder recibo = new StringBuilder();
        recibo.append("Compra realizada com sucesso!\n");
        recibo.append("Cobrado no cartão de final: ").append(cartaoMascarado).append("\n\n");
        recibo.append("------------------------------\n");

        if (bilhetes != null) {
            int numeroBilhete = 1;
            for (Bilhete b : bilhetes) {
                if (b == null) {
                    continue;
                }
                recibo.append("Bilhete #").append(numeroBilhete++).append("\n");
                recibo.append("Usuário: ").append(obterNomeUsuario(b)).append("\n");
                recibo.append("Sala: ").append(obterNomeSala(b)).append("\n");
                recibo.append("Sessão: ").append(b.getIndiceDaSessao() + 1).append("\n");
                recibo.append("Cadeira: ").append(b.getCadeira()).append("\n");
                recibo.append("------------------------------\n");
            }
        }
        return recibo.toString();
    }

    private String obterNomeUsuario(Bilhete bilhete) {
        return (bilhete != null && bilhete.getUsuario() != null)
                ? bilhete.getUsuario().getUser()
                : "Cliente";
    }

    private String obterNomeSala(Bilhete bilhete) {
        return (bilhete != null && bilhete.getSala() != null)
                ? bilhete.getSala().getNomeDaSala()
                : "";
    }
}