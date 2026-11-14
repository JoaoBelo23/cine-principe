package service;

import data.CriticosData;
import model.Critico;

public class CriticoDataAdapter implements ConsultorCritico {
    public Critico buscarPorUsuario(String username) {
        return CriticosData.pegar(username);
    }
}