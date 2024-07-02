
package backend;
import java.util.List;
import java.util.ListIterator;

public class Parser {
    private final List<Token> tokens;
    private final ListIterator<Token> iterator;
    private Token currentToken;
    
    public Parser(List<Token> tokens){
        this.tokens = tokens;
        this.iterator = tokens.listIterator();
        this.currentToken = iterator.next();
    }
    
    private void advance(){
        if(iterator.hasNext()){
            currentToken = iterator.next();
        }
    }
    
    private void match(Token.Type type){
        if(currentToken.getType() == type){
            advance();
        } else {
            throw new RuntimeException("Esperando " + type + "para encontrar " + currentToken.getType());
        }
    }
    
    public void parse(){
       match(Token.Type.LEFT_PAREN);
       parseDigits(3);
       match(Token.Type.RIGHT_PAREN);
       match(Token.Type.WHITESPACE);
       parseDigits(3);
       match(Token.Type.DASH);
       parseDigits(4);
       match(Token.Type.EOF);
       System.out.println("Analisis exitoso!"); 
    }
    
    private void parseDigits(int count){
        for(int i=0; i<count; i++){
            match(Token.Type.DIGIT);
        }
    }
}
