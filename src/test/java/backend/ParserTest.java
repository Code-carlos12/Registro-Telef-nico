package backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private static void validar(String numero) {
        new Parser(new Lexer(numero).tokenize()).parse();
    }

    @Test
    void lexerGeneraTokensEsperados() {
        List<Token> tokens = new Lexer("(1) 2-3").tokenize();
        assertEquals(List.of(
                Token.Type.LEFT_PAREN, Token.Type.DIGIT, Token.Type.RIGHT_PAREN,
                Token.Type.WHITESPACE, Token.Type.DIGIT, Token.Type.DASH,
                Token.Type.DIGIT, Token.Type.EOF),
                tokens.stream().map(Token::getType).toList());
    }

    @Test
    void lexerRechazaCaracterInvalido() {
        assertThrows(FormatoTelefonoException.class, () -> new Lexer("(55a)").tokenize());
    }

    @Test
    void aceptaNumeroValido() {
        assertDoesNotThrow(() -> validar("(555) 123-4567"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "555 123-4567",      // sin paréntesis
            "(55) 123-4567",     // pocos dígitos de área
            "(555)123-4567",     // sin espacio
            "(555) 1234567",     // sin guion
            "(555) 123-45678",   // dígitos de más
            "(555) 123-456",     // dígitos de menos
    })
    void rechazaNumerosInvalidos(String numero) {
        assertThrows(FormatoTelefonoException.class, () -> validar(numero));
    }
}
