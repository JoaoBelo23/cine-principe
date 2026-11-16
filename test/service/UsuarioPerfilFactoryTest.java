package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Critico;
import model.Estudante;
import model.Usuario;

public class UsuarioPerfilFactoryTest {

    private Usuario usuarioBase;
    private Critico criticoCadastrado;
    private UsuarioPerfilFactory factory;

    @BeforeEach
    public void setUp() {
        usuarioBase = new Usuario(
            "caio", "12345678900", "senha", 21, "M",
            "caio@email.com", "CAIO P MAIA", "4111222233334444", "123"
        );
        usuarioBase.setId(7);

        criticoCadastrado = new Critico(
            "caio", "12345678900", "senha", 21, "M",
            "caio@email.com", "CAIO P MAIA", "4111222233334444", "123", "Blog"
        );

        // Dublê do banco: só "caio" está cadastrado como crítico.
        ConsultorCritico bancoFalso = user -> "caio".equals(user) ? criticoCadastrado : null;
        factory = new UsuarioPerfilFactory(bancoFalso);
    }

    @Test
    public void perfilNenhumCriaUsuarioComPrecoPadrao() {
        Usuario criado = factory.criar("Nenhum", usuarioBase);

        assertEquals(Usuario.class, criado.getClass());
        assertInstanceOf(PrecoPadraoStrategy.class, criado.getEstrategiaPreco());
    }

    @Test
    public void perfilEstudanteCriaEstudanteComMeiaEntrada() {
        Usuario criado = factory.criar("Estudante", usuarioBase);

        assertInstanceOf(Estudante.class, criado);
        assertInstanceOf(PrecoEstudanteStrategy.class, criado.getEstrategiaPreco());
    }

    @Test
    public void perfilCriticoBuscaCriticoNoBanco() {
        Usuario criado = factory.criar("Crítico", usuarioBase);

        assertSame(criticoCadastrado, criado);
        assertInstanceOf(PrecoCriticoStrategy.class, criado.getEstrategiaPreco());
        assertEquals("Blog", ((Critico) criado).getOrigem());
    }

    @Test
    public void criticoNaoCadastradoPagaPrecoPadrao() {
        Usuario outro = new Usuario(
            "joao", "999", "senha", 30, "M",
            "joao@email.com", "JOAO", "5555666677778888", "321"
        );

        Usuario criado = factory.criar("Crítico", outro);

        assertEquals(Usuario.class, criado.getClass());
        assertInstanceOf(PrecoPadraoStrategy.class, criado.getEstrategiaPreco());
    }

    @Test
    public void perfilNuloOuDesconhecidoCriaUsuarioPadrao() {
        assertEquals(Usuario.class, factory.criar(null, usuarioBase).getClass());
        assertEquals(Usuario.class, factory.criar("Idoso", usuarioBase).getClass());
    }

    @Test
    public void perfilIgnoraMaiusculasEEspacos() {
        assertInstanceOf(Estudante.class, factory.criar("  estudante ", usuarioBase));
    }

    @Test
    public void copiaOsDadosDoUsuarioBase() {
        Usuario criado = factory.criar("Estudante", usuarioBase);

        assertNotSame(usuarioBase, criado);
        assertEquals(usuarioBase.getId(), criado.getId());
        assertEquals(usuarioBase.getUser(), criado.getUser());
        assertEquals(usuarioBase.getCpf(), criado.getCpf());
        assertEquals(usuarioBase.getSenha(), criado.getSenha());
        assertEquals(usuarioBase.getIdade(), criado.getIdade());
        assertEquals(usuarioBase.getSexo(), criado.getSexo());
        assertEquals(usuarioBase.getEmail(), criado.getEmail());
        assertEquals(usuarioBase.getNome_do_cartao(), criado.getNome_do_cartao());
        assertEquals(usuarioBase.getNumero_do_cartao(), criado.getNumero_do_cartao());
        assertEquals(usuarioBase.getCodigo_verificador_do_cartao(), criado.getCodigo_verificador_do_cartao());
    }

    @Test
    public void novoPerfilPodeSerRegistradoSemAlterarAFactory() {
        factory.registrarPerfil("Idoso", base -> new Estudante(
            base.getUser(), base.getCpf(), base.getSenha(), base.getIdade(), base.getSexo(),
            base.getEmail(), base.getNome_do_cartao(), base.getNumero_do_cartao(),
            base.getCodigo_verificador_do_cartao()
        ));

        assertInstanceOf(Estudante.class, factory.criar("Idoso", usuarioBase));
    }

    @Test
    public void usuarioBaseNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> factory.criar("Estudante", null));
    }
}
