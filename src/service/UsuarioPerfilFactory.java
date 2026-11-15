package service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import model.Critico;
import model.Estudante;
import model.Usuario;

/**
 * Simple Factory responsável por criar o tipo de usuário de acordo com o
 * perfil de desconto escolhido na TelaDesconto ("Nenhum", "Estudante" ou "Crítico").
 *
 * Antes essa lógica ficava no ControlCheckout, com um if/else por perfil e
 * construtores de 9 parâmetros. Agora o checkout só pede o usuário à fábrica,
 * e a estratégia de preço vem junto, porque Estudante e Critico já configuram
 * a sua EstrategiaPrecoBilhete no construtor.
 */
public class UsuarioPerfilFactory {

    public static final String PERFIL_ESTUDANTE = "Estudante";
    public static final String PERFIL_CRITICO = "Crítico";

    private static final UsuarioPerfilFactory PADRAO = new UsuarioPerfilFactory();

    private final ConsultorCritico consultorCritico;
    private final Map<String, Function<Usuario, Usuario>> criadoresPorPerfil = new HashMap<>();

    public UsuarioPerfilFactory() {
        this(new CriticoDataAdapter());
    }

    /** Permite trocar o acesso ao banco (CriticosData) por um dublê nos testes. */
    public UsuarioPerfilFactory(ConsultorCritico consultorCritico) {
        this.consultorCritico = consultorCritico;
        registrarPerfil(PERFIL_ESTUDANTE, this::criarEstudante);
        registrarPerfil(PERFIL_CRITICO, this::buscarCritico);
    }

    /**
     * Ponto de entrada usado pela CheckoutFacade
     * (UsuarioPerfilFactory::criarUsuarioPorPerfil).
     */
    public static Usuario criarUsuarioPorPerfil(String perfil, Usuario usuarioBase) {
        return PADRAO.criar(perfil, usuarioBase);
    }

    /**
     * Um perfil novo (ex.: "Idoso") entra aqui, sem mexer no checkout.
     */
    public void registrarPerfil(String perfil, Function<Usuario, Usuario> criador) {
        if (perfil != null && criador != null) {
            criadoresPorPerfil.put(normalizar(perfil), criador);
        }
    }

    public Usuario criar(String perfil, Usuario usuarioBase) {
        if (usuarioBase == null) {
            throw new IllegalArgumentException("Usuário base não pode ser nulo.");
        }

        Function<Usuario, Usuario> criador =
                (perfil == null) ? null : criadoresPorPerfil.get(normalizar(perfil));

        Usuario criado = (criador != null) ? criador.apply(usuarioBase) : null;

        // Perfil "Nenhum", desconhecido ou crítico não cadastrado: preço normal.
        return (criado != null) ? criado : criarUsuarioPadrao(usuarioBase);
    }

    private Usuario criarUsuarioPadrao(Usuario base) {
        Usuario usuario = new Usuario(
                base.getUser(), base.getCpf(), base.getSenha(), base.getIdade(), base.getSexo(),
                base.getEmail(), base.getNome_do_cartao(), base.getNumero_do_cartao(),
                base.getCodigo_verificador_do_cartao());
        usuario.setId(base.getId());
        return usuario;
    }

    private Usuario criarEstudante(Usuario base) {
        Estudante estudante = new Estudante(
                base.getUser(), base.getCpf(), base.getSenha(), base.getIdade(), base.getSexo(),
                base.getEmail(), base.getNome_do_cartao(), base.getNumero_do_cartao(),
                base.getCodigo_verificador_do_cartao());
        estudante.setId(base.getId());
        return estudante;
    }

    // O crítico vem do banco (CriticosData.pegar, via CriticoDataAdapter),
    // pois só ali existe o campo "origem".
    private Usuario buscarCritico(Usuario base) {
        if (base.getUser() == null) {
            return null;
        }
        Critico critico = consultorCritico.buscarPorUsuario(base.getUser());
        return critico;
    }

    private static String normalizar(String perfil) {
        return perfil.trim().toUpperCase();
    }
}
