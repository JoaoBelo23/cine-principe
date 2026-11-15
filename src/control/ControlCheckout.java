package control;

import gui.Navegacao;
import gui.TelaCheckout;
import gui.TelaCritica;
import model.DadosCheckout;
import service.CheckoutFacade;

public class ControlCheckout {

    private final TelaCheckout tela;
    private final CheckoutFacade checkoutFacade;
    private DadosCheckout dadosAtuais;

    public ControlCheckout(TelaCheckout tela) {
        this(tela, new CheckoutFacade());
    }

    public ControlCheckout(TelaCheckout tela, CheckoutFacade checkoutFacade) {
        this.tela = tela;
        this.checkoutFacade = checkoutFacade;
        processarValoresIniciais();
    }

    private void processarValoresIniciais() {
        this.dadosAtuais = checkoutFacade.processarCheckout(
                tela.getPerfil(),
                tela.getBilhetes(),
                tela.getValorTotalProdutos(),
                tela.getCupom()
        );

        tela.setUsuarioFinal(dadosAtuais.getUsuarioFinal());
        tela.setValorTotalBilhetes(dadosAtuais.getValorTotalBilhetes());
        tela.setValorFinalCalculado(dadosAtuais.getValorFinal());
    }

    public void finalizarCompra() {
        String recibo = checkoutFacade.gerarReciboCompra(dadosAtuais, tela.getBilhetes());
        tela.exibirRecibo(recibo);

        if (dadosAtuais != null && dadosAtuais.exigeCritica()) {
            TelaCritica telaCritica = new TelaCritica(
                    dadosAtuais.getCritico(),
                    dadosAtuais.getFilme()
            );
            Navegacao.exibir(telaCritica);
            return;
        }

        Navegacao.fechar();
    }

    public void cancelarCompra() {
        if (tela.getTelaDesconto() != null) {
            Navegacao.voltar();
        } else {
            Navegacao.fechar();
        }
    }
}