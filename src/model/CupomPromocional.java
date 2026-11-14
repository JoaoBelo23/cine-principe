package model;
public enum CupomPromocional {
    FIDELIDADE(0.5),
    BRADESCO(0.7),
    ELO(0.8);

    private double desconto;

    CupomPromocional(double desconto) {
        this.desconto = desconto;
    }

    public double getDesconto() {
        return desconto;
    }

    public double aplicarDesconto(double valorTotal) {
        if(valorTotal <= 0) return 0;

        return valorTotal * this.desconto;
    }

    public static CupomPromocional buscarPorCodigo(String codigo) {
        if(codigo == null || codigo.isEmpty()) {
            return null;
        }

        for (CupomPromocional cupom : values()) {
            if(cupom.name().equalsIgnoreCase(codigo)) {
                return cupom;
            }
        }
        return null;
    }

}