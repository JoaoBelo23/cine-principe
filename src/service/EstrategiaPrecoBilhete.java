package service;
import java.util.List;
import model.Bilhete;

public interface EstrategiaPrecoBilhete {

    double calcularPreco(List<Bilhete> bilhetes);
    
}
