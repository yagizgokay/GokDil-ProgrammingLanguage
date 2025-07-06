// === src/lexer/Lexer.java ===
package lexer;

import java.util.*;
import java.util.regex.*;

public class Lexer {
    private static class Rule {
        public final Pattern pattern;
        public final TokenType type;

        public Rule(String regex, TokenType type) {
            this.pattern = Pattern.compile("^" + regex);
            this.type = type;
        }
    }

    private final List<Rule> rules = new ArrayList<>();
    private final Map<String, TokenType> keywords = new HashMap<>();

    public Lexer() {
        keywords.put("tanımla", TokenType.KEYWORD_TANIMLA);
        keywords.put("tür", TokenType.KEYWORD_TIPI);
        keywords.put("başlangıç", TokenType.KEYWORD_BASLANGIC);
        keywords.put("ise", TokenType.KEYWORD_ISE);
        keywords.put("değilse", TokenType.KEYWORD_DEĞİLSE);
        keywords.put("böyleyken", TokenType.KEYWORD_BÖYLEYKEN);
        keywords.put("tekrar", TokenType.KEYWORD_TEKRAR);
        keywords.put("yazdır", TokenType.KEYWORD_YAZDIR);
        keywords.put("fonksiyon", TokenType.KEYWORD_FONKSIYON);
        keywords.put("tamsayı", TokenType.DATA_TYPE_TAM);
        keywords.put("ondalık", TokenType.DATA_TYPE_ONDALIK);
        keywords.put("yazı", TokenType.DATA_TYPE_YAZI);
        keywords.put("mantıksal", TokenType.DATA_TYPE_MANTIKAL);
        keywords.put("doğru", TokenType.KEYWORD_DOGRU);
        keywords.put("yanlış", TokenType.KEYWORD_YANLIS);
        keywords.put("dön", TokenType.KEYWORD_GERI_VER);
        keywords.put("ve", TokenType.LOGIC_OP_VE);
        keywords.put("veya", TokenType.LOGIC_OP_VEYA);

        rules.add(new Rule("\\?\\?[^\\?]*\\?\\?", TokenType.INPUT_PROMPT));
        rules.add(new Rule("@[^\\n]*", TokenType.COMMENT));
        rules.add(new Rule("[0-9]+\\.[0-9]+", TokenType.FLOAT_LIT));
        rules.add(new Rule("[0-9]+", TokenType.INT_LIT));
        rules.add(new Rule("\"[^\"]*\"", TokenType.STRING_LITERAL));
        rules.add(new Rule("==", TokenType.COMP_OP_ESITTIR));
        rules.add(new Rule(">=", TokenType.COMP_OP_BUYUK_ESIT));
        rules.add(new Rule("<=", TokenType.COMP_OP_KUCUK_ESIT));
        rules.add(new Rule(">", TokenType.COMP_OP_BUYUKTUR));
        rules.add(new Rule("<", TokenType.COMP_OP_KUCUKTUR));
        rules.add(new Rule("=", TokenType.ASSIGN_OP));
        rules.add(new Rule("\\+", TokenType.BIN_OP_ARTI));
        rules.add(new Rule("-", TokenType.BIN_OP_EKSI));
        rules.add(new Rule("\\*", TokenType.BIN_OP_CARPI));
        rules.add(new Rule("/", TokenType.BIN_OP_BOLU));
        rules.add(new Rule("%", TokenType.BIN_OP_MODULO));
        rules.add(new Rule("\\(", TokenType.LEFT_PAREN));
        rules.add(new Rule("\\)", TokenType.RIGHT_PAREN));
        rules.add(new Rule("\\{", TokenType.LEFT_BRACE));
        rules.add(new Rule("}", TokenType.RIGHT_BRACE));
        rules.add(new Rule("\\$", TokenType.DOLLAR));
        rules.add(new Rule(",", TokenType.COMMA));
        rules.add(new Rule("[a-zA-ZçğıöşüÇĞİÖŞÜ_][a-zA-Z0-9çğıöşüÇĞİÖŞÜ_]*", TokenType.IDENT));
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int line = 1;

        while (!input.isEmpty()) {
            input = input.stripLeading();
            if (input.isEmpty()) break;
            
            System.out.println("Kalan metin: '" + input + "'");
            boolean matched = false;

            // Önce anahtar kelimeleri kontrol et
            for (Map.Entry<String, TokenType> entry : keywords.entrySet()) {
                String keyword = entry.getKey();
                if (input.startsWith(keyword) && 
                    (input.length() == keyword.length() || 
                     !Character.isLetterOrDigit(input.charAt(keyword.length())))) {
                    tokens.add(new Token(entry.getValue(), keyword, line));
                    input = input.substring(keyword.length());
                    matched = true;
                    System.out.println("Anahtar kelime bulundu: " + entry.getValue());
                    break;
                }

            }

            if (!matched) {
                // Sonra diğer kuralları kontrol et
                for (Rule rule : rules) {
                    Matcher matcher = rule.pattern.matcher(input);
                    if (matcher.find()) {
                        String lexeme = matcher.group();
                        TokenType type = rule.type;
                        System.out.println("Eşleşme bulundu: '" + lexeme + "' -> " + type);
                        tokens.add(new Token(type, lexeme, line));
                        input = input.substring(lexeme.length());
                        matched = true;
                        break;
                    }
                }
            }

            if (!matched) {
                String badChar = input.substring(0, 1);
                System.out.println("Eşleşme bulunamadı, karakter atlanıyor: '" + badChar + "'");
                tokens.add(new Token(TokenType.ERROR, badChar, line));
                input = input.substring(1);
            }

            if (input.startsWith("\n")) {
                line++;
                input = input.substring(1);
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }
}



