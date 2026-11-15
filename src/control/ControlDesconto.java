package control;

import gui.Navegacao;
import gui.TelaCheckout;
import gui.TelaDesconto;
import model.CupomPromocional;
import model.Usuario;
import service.ValidadorDesconto;

public class ControlDesconto {

    private final TelaDesconto tela;
    private final ValidadorDesconto validadorDesconto;

    public ControlDesconto(TelaDesconto tela) {
        this(tela, new ValidadorDesconto());
    }

    public ControlDesconto(TelaDesconto tela, ValidadorDesconto validadorDesconto) {
        this.tela = tela;
        this.validadorDesconto = validadorDesconto;
    }

    public void voltar() {
        Navegacao.voltar();
    }

    public void avancar(Usuario usuario) {
        String perfil = (String) tela.getComboPerfil().getSelectedItem();
        String textoCupom = tela.getTxtCupom().getText().trim();

        if (!validadorDesconto.isPerfilValido(perfil, usuario)) {
            tela.exibirMensagemAviso("Usuário não elegível para o perfil selecionado.");
            return;
        }
        tela.setPerfilSelecionado(perfil);

        if (!textoCupom.isEmpty()) {
            CupomPromocional cupom = validadorDesconto.validarCupom(textoCupom);
            if (cupom == null) {
                tela.exibirMensagemAviso("Cupom inválido! Verifique o código ou deixe o campo em branco.");
                return;
            }
            tela.setCupomAplicado(cupom);
        } else {
            tela.setCupomAplicado(null);
        }

        TelaCheckout telaCheckout = new TelaCheckout(tela);
        Navegacao.exibir(telaCheckout);
    }
}