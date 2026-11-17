package service;

import model.Bilhete;
import model.Critico;
import model.CupomPromocional;
import model.DadosCheckout;
import model.Filme;
import model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class CheckoutFacade {

    private final FormatadorRecibo formatadorRecibo;
    private final UsuarioPerfilFactory perfilFactory;

    public CheckoutFacade() {
        this(new FormatadorRecibo(), new UsuarioPerfilFactory());
    }

    public CheckoutFacade(FormatadorRecibo formatadorRecibo, UsuarioPerfilFactory perfilFactory) {
        this.formatadorRecibo = formatadorRecibo;
        this.perfilFactory = perfilFactory;
    }

    public DadosCheckout processarCheckout(String perfil, List<Bilhete> bilhetes, double valorTotalProdutos, CupomPromocional cupom) {

        if (bilhetes == null || bilhetes.isEmpty()) {
            throw new IllegalArgumentException("A lista de bilhetes não pode ser vazia.");
        }
        for (Bilhete bilhete : bilhetes) {
            if (bilhete == null) {
                throw new IllegalArgumentException("A lista de bilhetes não pode conter elementos nulos.");
            }
        }

        Usuario usuarioBase = bilhetes.get(0).getUsuario();
        Usuario usuarioFinal = perfilFactory.criar(perfil, usuarioBase);    

        ArrayList<Bilhete> listaBilhetes = new ArrayList<>(bilhetes);
        double valorTotalBilhetes = usuarioFinal.comprarBilhetes(listaBilhetes);
        double valorBruto = usuarioFinal.realizarCompra(valorTotalBilhetes, valorTotalProdutos);
        double valorFinal = (cupom != null) ? cupom.aplicarDesconto(valorBruto) : valorBruto;

        Critico critico = usuarioFinal.comoCritico();
        Filme filme = extrairFilmePrincipal(bilhetes.get(0));

        return new DadosCheckout(usuarioFinal, critico, filme, valorTotalBilhetes, valorFinal);
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