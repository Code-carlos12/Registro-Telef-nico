
package backend;

public class Token {
    public static enum Type{
        LEFT_PAREN, RIGHT_PAREN, DIGIT, DASH, WHITESPACE, EOF
    }
    
    private Type type;
    private String lexeme;
    
    
    public Token(Type type, String lexame){
        this.type = type;
        this.lexeme = lexame;
    }

    public Type getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", lexeme=" + lexeme + '\''+'}';
    }
    
}
