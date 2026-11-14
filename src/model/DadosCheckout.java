package model;

public class DadosCheckout {
    private final Usuario usuarioFinal;
    private final Critico critico;
    private final Filme filme;
    private final double valorTotalBilhetes;
    private final double valorFinal;

    public DadosCheckout(
            Usuario usuarioFinal,
            Critico critico,
            Filme filme,
            double valorTotalBilhetes,
            double valorFinal) {
        this.usuarioFinal = usuarioFinal;
        this.critico = critico;
        this.filme = filme;
        this.valorTotalBilhetes = valorTotalBilhetes;
        this.valorFinal = valorFinal;
    }

    public Usuario getUsuarioFinal() {
        return usuarioFinal;
    }

    public boolean exigeCritica() {
        return critico != null && filme != null;
    }

    public Critico getCritico() {
        return critico;
    }

    public Filme getFilme() {
        return filme;
    }

    public double getValorTotalBilhetes() {
        return valorTotalBilhetes;
    }

    public double getValorFinal() {
        return valorFinal;
    }
}