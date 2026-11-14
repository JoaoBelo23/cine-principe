package service;
import java.util.List;
import model.Bilhete;

public class PrecoCriticoStrategy implements EstrategiaPrecoBilhete {

    @Override
    public double calcularPreco(List<Bilhete> bilhetes) {

        if (bilhetes == null || bilhetes.isEmpty()) {
            return 0.0;
        }

        if(bilhetes.size() < 3) return 0.0;

        return (bilhetes.size() - 2) * bilhetes.get(0).getValorComTaxaSala();
    }
    
}
