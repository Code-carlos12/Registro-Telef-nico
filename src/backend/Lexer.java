
package backend;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
   private final String input;
   private final List<Token> tokens = new ArrayList<>();
   private int position = 0;
   
   public Lexer(String input){
       this.input = input;
   }
   
   public List<Token> tokenize(){
       while (position < input.length()){
           char currentChar = input.charAt(position);
           switch (currentChar) {
               case '(':
                   tokens.add(new Token(Token.Type.LEFT_PAREN, String.valueOf(currentChar)));
                   position++;
                   break;  
               case ')':
                    tokens.add(new Token(Token.Type.RIGHT_PAREN, String.valueOf(currentChar)));
                    position++;
                    break;
                case '-':
                    tokens.add(new Token(Token.Type.DASH, String.valueOf(currentChar)));
                    position++;
                    break;
                case ' ':
                    tokens.add(new Token(Token.Type.WHITESPACE, String.valueOf(currentChar)));
                    position++;
                    break; 
               default:
                   if(Character.isDigit(currentChar)){
                       tokens.add(new Token(Token.Type.DIGIT, String.valueOf(currentChar)));
                       position++;
                   } else{
                       throw new RuntimeException("Carracter incorrecto: " + currentChar);
                   }
                   break;
           }
       }
       tokens.add(new Token(Token.Type.EOF, ""));
       return tokens;
   }
}
