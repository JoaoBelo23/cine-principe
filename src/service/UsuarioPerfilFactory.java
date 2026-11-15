package service;

import model.Usuario;

public class UsuarioPerfilFactory {
    public static Usuario criarUsuarioPorPerfil(String perfil, Usuario usuarioBase) {
        return usuarioBase;
    }
}