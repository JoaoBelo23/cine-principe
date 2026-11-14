package service;
import java.util.List;
import model.Bilhete;

public class PrecoEstudanteStrategy implements EstrategiaPrecoBilhete {

    @Override
    public double calcularPreco(List<Bilhete> bilhetes) {

        if (bilhetes == null || bilhetes.isEmpty()) {
            return 0.0;
        }

        return (bilhetes.size() * bilhetes.get(0).getValorComTaxaSala())/2;
    }

    
}
