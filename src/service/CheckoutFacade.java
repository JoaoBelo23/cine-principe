package service;

import model.Bilhete;
import model.Critico;
import model.CupomPromocional;
import model.DadosCheckout;
import model.Filme;
import model.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CheckoutFacade {

    private final FormatadorRecibo formatadorRecibo;
    private final BiFunction<String, Usuario, Usuario> criadorUsuarioPorPerfil;

    public CheckoutFacade() {
        this(new FormatadorRecibo(), UsuarioPerfilFactory::criarUsuarioPorPerfil);
    }

    public CheckoutFacade(
            FormatadorRecibo formatadorRecibo,
            BiFunction<String, Usuario, Usuario> criadorUsuarioPorPerfil) {
        this.formatadorRecibo = formatadorRecibo;
        this.criadorUsuarioPorPerfil = criadorUsuarioPorPerfil;
    }

    public DadosCheckout processarCheckout(
            String perfil,
            List<Bilhete> bilhetes,
            double valorTotalProdutos,
            CupomPromocional cupom) {

        if (bilhetes == null || bilhetes.isEmpty()) {
            throw new IllegalArgumentException("A lista de bilhetes não pode ser vazia.");
        }

        Usuario usuarioBase = bilhetes.get(0).getUsuario();
        Usuario usuarioFinal = criadorUsuarioPorPerfil.apply(perfil, usuarioBase);

        ArrayList<Bilhete> listaBilhetes = new ArrayList<>(bilhetes);
        double valorTotalBilhetes = usuarioFinal.comprarBilhetes(listaBilhetes);
        double valorFinal = (cupom != null)
                ? usuarioFinal.realizarCompra(valorTotalBilhetes, valorTotalProdutos, cupom)
                : usuarioFinal.realizarCompra(valorTotalBilhetes, valorTotalProdutos);

        Critico critico = usuarioFinal.comoCritico();
        Filme filme = extrairFilmePrincipal(bilhetes.get(0));

        return new DadosCheckout(
                usuarioFinal,
                critico,
                filme,
                valorTotalBilhetes,
                valorFinal
        );
    }

    public String gerarReciboCompra(DadosCheckout dadosCheckout, List<Bilhete> bilhetes) {
        Usuario usuario = (dadosCheckout != null) ? dadosCheckout.getUsuarioFinal() : null;
        return formatadorRecibo.gerarRecibo(usuario, bilhetes);
    }

    private Filme extrairFilmePrincipal(Bilhete bilhete) {
        if (bilhete == null || bilhete.getSessao() == null) {
            return null;
        }
        return bilhete.getSessao().getFilme();
    }
}