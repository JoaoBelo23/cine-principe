package service;

import model.CupomPromocional;
import model.Usuario;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ValidadorDesconto {

    private final Map<String, Predicate<Usuario>> regrasPorPerfil = new HashMap<>();

    public ValidadorDesconto() {
        this(new CriticoDataAdapter());
    }

    public ValidadorDesconto(ConsultorCritico consultorCritico) {
        registrarRegraPerfil("Crítico", usuario ->
                usuario != null
                && usuario.getUser() != null
                && consultorCritico.buscarPorUsuario(usuario.getUser()) != null
        );
    }

    public void registrarRegraPerfil(String perfil, Predicate<Usuario> regra) {
        if (perfil != null && regra != null) {
            regrasPorPerfil.put(perfil.trim().toUpperCase(), regra);
        }
    }

    public boolean isPerfilValido(String perfil, Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        if (perfil == null) {
            return true;
        }
        Predicate<Usuario> regra = regrasPorPerfil.get(perfil.trim().toUpperCase());
        return regra == null || regra.test(usuario);
    }

    public CupomPromocional validarCupom(String codigoCupom) {
        return CupomPromocional.buscarPorCodigo(codigoCupom);
    }
}