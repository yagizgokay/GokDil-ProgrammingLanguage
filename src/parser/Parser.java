// === src/parser/Parser.java ===
package parser;

import java.util.*;
import java.util.stream.Collectors;
import lexer.*;

public class Parser {
    private List<Token> tokens;
    private int current = 0;

    public ASTNode parse(List<Token> tokens) {
        this.tokens = tokens;
        if (tokens == null || tokens.isEmpty()) {
            throw new RuntimeException("⚠️ Token listesi boş. Sözdizimi çözümlemesi yapılamaz.");
        }
        return parseProgram();
    }

    private ASTNode parseProgram() {
        ASTNode program = new ASTNode("Program", "");
        while (!isAtEnd()) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                program.addChild(stmt);
            }
        }
        return program;
    }

    private ASTNode parseStatement() {
        if (check(TokenType.KEYWORD_TANIMLA)) return parseVariableDeclaration();
        if (check(TokenType.IDENT) && checkNext(TokenType.ASSIGN_OP)) return parseAssignment();
        if (check(TokenType.KEYWORD_YAZDIR)) {
            ASTNode print = parsePrint();
            return print;
        }
        if (check(TokenType.KEYWORD_BÖYLEYKEN)) return parseWhile();
        if (check(TokenType.KEYWORD_ISE)) return parseIf();
        if (check(TokenType.KEYWORD_DEĞİLSE)) {
            if (checkNext(TokenType.KEYWORD_ISE)) {
                advance(); // KEYWORD_DEĞİLSE'yi tüket
                return parseIf();
            }
            advance(); // KEYWORD_DEĞİLSE'yi tüket
            return parseBlock();
        }
        if (check(TokenType.KEYWORD_FONKSIYON)) return parseFunction();
        if (check(TokenType.KEYWORD_GERI_VER)) return parseReturn();
        if (check(TokenType.IDENT) && checkNext(TokenType.LEFT_PAREN)) {
            ASTNode call = parseCall();
            return call;
        }
        if (check(TokenType.COMMENT)) return parseComment();
        if (check(TokenType.EOF) || check(TokenType.DOLLAR) || check(TokenType.RIGHT_BRACE)) {
            if (check(TokenType.DOLLAR)) advance();
            return null;
        }
        throw error(peek(), "Geçersiz ifade: " + peek().type);
    }

    private ASTNode parseVariableDeclaration() {
        consume(TokenType.KEYWORD_TANIMLA);
        Token name = consume(TokenType.IDENT);
        consume(TokenType.KEYWORD_TIPI);
        Token typeToken = consume(TokenType.DATA_TYPE_TAM, TokenType.DATA_TYPE_ONDALIK, 
                                TokenType.DATA_TYPE_YAZI, TokenType.DATA_TYPE_MANTIKAL);
        consume(TokenType.KEYWORD_BASLANGIC);
        ASTNode expr = parseExpression();
        consume(TokenType.DOLLAR);
        ASTNode varDecl = new ASTNode("VarDecl", name.value);
        varDecl.addChild(new ASTNode("DataType", typeToken.value));
        varDecl.addChild(expr);
        return varDecl;
    }

    private ASTNode parseAssignment() {
        Token name = consume(TokenType.IDENT);
        consume(TokenType.ASSIGN_OP);
        ASTNode right = parseExpression();
        consume(TokenType.DOLLAR);
        ASTNode binOp = new ASTNode("BinOp", "=");
        binOp.addChild(new ASTNode("Var", name.value));
        binOp.addChild(right);
        return binOp;
    }

    private ASTNode parsePrint() {
        consume(TokenType.KEYWORD_YAZDIR);
        consume(TokenType.LEFT_PAREN);
        ASTNode expr = parseExpression();
        consume(TokenType.RIGHT_PAREN);
        consume(TokenType.DOLLAR);
        ASTNode print = new ASTNode("Print", "");
        print.addChild(expr);
        return print;
    }

    private ASTNode parseIf() {
        consume(TokenType.KEYWORD_ISE);
        consume(TokenType.LEFT_PAREN);
        ASTNode condition = parseExpression();
        consume(TokenType.RIGHT_PAREN);
        ASTNode thenBlock = parseBlock();

        ASTNode ifNode = new ASTNode("If", "");
        ifNode.addChild(condition);
        ifNode.addChild(thenBlock);

        if (match(TokenType.KEYWORD_DEĞİLSE)) {
            if (check(TokenType.KEYWORD_ISE)) {
                // Bu bir else-if durumu
                advance(); // KEYWORD_ISE'yi tüket
                consume(TokenType.LEFT_PAREN);
                ASTNode elseIfCondition = parseExpression();
                consume(TokenType.RIGHT_PAREN);
                ASTNode elseIfBlock = parseBlock();
                
                // Yeni bir If düğümü oluştur
                ASTNode elseIfNode = new ASTNode("If", "");
                elseIfNode.addChild(elseIfCondition);
                elseIfNode.addChild(elseIfBlock);
                
                // Ana if düğümüne else bloğu olarak ekle
                ifNode.addChild(elseIfNode);
            } else {
                // Normal else durumu
                ASTNode elseBlock = parseBlock();
                ifNode.addChild(elseBlock);
            }
        }

        return ifNode;
    }

    private ASTNode parseWhile() {
        consume(TokenType.KEYWORD_BÖYLEYKEN);
        consume(TokenType.LEFT_PAREN);
        ASTNode condition = parseExpression();
        consume(TokenType.RIGHT_PAREN);
        ASTNode body = parseBlock();
        ASTNode whileNode = new ASTNode("While", "");
        whileNode.addChild(condition);
        whileNode.addChild(body);
        return whileNode;
    }

    private ASTNode parseFunction() {
        consume(TokenType.KEYWORD_FONKSIYON);
        Token name = consume(TokenType.IDENT);
        consume(TokenType.LEFT_PAREN);
        
        List<String> params = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                Token param = consume(TokenType.IDENT);
                params.add(param.value);
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN);
        
        ASTNode body = parseBlock();
        
        ASTNode function = new ASTNode("Function", name.value);
        for (String param : params) {
            ASTNode paramNode = new ASTNode("Param", param);
            function.addChild(paramNode);
        }
        function.addChild(body);
        
        return function;
    }

    private ASTNode parseCall() {
        Token name = consume(TokenType.IDENT);
        consume(TokenType.LEFT_PAREN);
        List<ASTNode> args = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                args.add(parseExpression());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN);
        
        ASTNode call = new ASTNode("Call", name.value);
        for (ASTNode arg : args) {
            call.addChild(arg);
        }
        return call;
    }

    private ASTNode parseBlock() {
        consume(TokenType.LEFT_BRACE);
        ASTNode block = new ASTNode("Block", "");
        while (!check(TokenType.RIGHT_BRACE)) {
            ASTNode stmt = parseStatement();
            if (stmt != null) {
                block.addChild(stmt);
            }
        }
        consume(TokenType.RIGHT_BRACE);
        return block;
    }

    private ASTNode parseExpression() {
        return parseLogicalOr();
    }

    private ASTNode parseLogicalOr() {
        ASTNode left = parseLogicalAnd();
        
        while (match(TokenType.LOGIC_OP_VEYA)) {
            Token operator = previous();
            ASTNode right = parseLogicalAnd();
            ASTNode binOp = new ASTNode("BinOp", "veya");
            binOp.addChild(left);
            binOp.addChild(right);
            left = binOp;
        }
        
        return left;
    }

    private ASTNode parseLogicalAnd() {
        ASTNode left = parseEquality();
        
        while (match(TokenType.LOGIC_OP_VE)) {
            Token operator = previous();
            ASTNode right = parseEquality();
            ASTNode binOp = new ASTNode("BinOp", "ve");
            binOp.addChild(left);
            binOp.addChild(right);
            left = binOp;
        }
        
        return left;
    }

    private ASTNode parseEquality() {
        ASTNode left = parseComparison();
        
        while (match(TokenType.COMP_OP_ESITTIR, TokenType.COMP_OP_FARKLI)) {
            Token operator = previous();
            ASTNode right = parseComparison();
            String opValue = operator.type == TokenType.COMP_OP_ESITTIR ? "==" : "!=";
            ASTNode binOp = new ASTNode("BinOp", opValue);
            binOp.addChild(left);
            binOp.addChild(right);
            left = binOp;
        }
        
        return left;
    }

    private ASTNode parseComparison() {
        ASTNode left = parseAdditive();
        
        while (match(TokenType.COMP_OP_BUYUKTUR, TokenType.COMP_OP_KUCUKTUR,
                    TokenType.COMP_OP_BUYUK_ESIT, TokenType.COMP_OP_KUCUK_ESIT)) {
            Token operator = previous();
            ASTNode right = parseAdditive();
            String opValue = switch (operator.type) {
                case COMP_OP_BUYUKTUR -> ">";
                case COMP_OP_KUCUKTUR -> "<";
                case COMP_OP_BUYUK_ESIT -> ">=";
                case COMP_OP_KUCUK_ESIT -> "<=";
                default -> operator.value;
            };
            ASTNode binOp = new ASTNode("BinOp", opValue);
            binOp.addChild(left);
            binOp.addChild(right);
            left = binOp;
        }
        
        return left;
    }

    private ASTNode parseAdditive() {
        ASTNode left = parseMultiplicative();
        
        while (match(TokenType.BIN_OP_ARTI, TokenType.BIN_OP_EKSI)) {
            Token operator = previous();
            ASTNode right = parseMultiplicative();
            String opValue = operator.type == TokenType.BIN_OP_ARTI ? "+" : "-";
            ASTNode binOp = new ASTNode("BinOp", opValue);
            binOp.addChild(left);
            binOp.addChild(right);
            left = binOp;
        }
        
        return left;
    }

    private ASTNode parseMultiplicative() {
        ASTNode left = parsePrimary();
        
        while (match(TokenType.BIN_OP_CARPI, TokenType.BIN_OP_BOLU, TokenType.BIN_OP_MODULO)) {
            Token operator = previous();
            ASTNode right = parsePrimary();
            String opValue = switch (operator.type) {
                case BIN_OP_CARPI -> "*";
                case BIN_OP_BOLU -> "/";
                case BIN_OP_MODULO -> "%";
                default -> operator.value;
            };
            ASTNode binOp = new ASTNode("BinOp", opValue);
            binOp.addChild(left);
            binOp.addChild(right);
            left = binOp;
        }
        
        return left;
    }

    private ASTNode parsePrimary() {
        if (match(TokenType.INT_LIT)) {
            return new ASTNode("IntLit", previous().value);
        }
        if (match(TokenType.FLOAT_LIT)) {
            return new ASTNode("FloatLit", previous().value);
        }
        if (match(TokenType.STRING_LITERAL)) {
            return new ASTNode("StringLit", previous().value);
        }
        if (match(TokenType.KEYWORD_DOGRU)) {
            return new ASTNode("BooleanLit", "doğru");
        }
        if (match(TokenType.KEYWORD_YANLIS)) {
            return new ASTNode("BooleanLit", "yanlış");
        }
        if (check(TokenType.INPUT_PROMPT)) {
            Token value = consume(TokenType.INPUT_PROMPT);
            String promptText = value.value.substring(2, value.value.length() - 2);
            ASTNode inputNode = new ASTNode("InputPrompt", promptText);
            return inputNode;
        }
        if (check(TokenType.IDENT)) {
            if (checkNext(TokenType.LEFT_PAREN)) {
                return parseCall();
            }
            Token value = consume(TokenType.IDENT);
            return new ASTNode("Var", value.value);
        }
        if (check(TokenType.LEFT_PAREN)) {
            consume(TokenType.LEFT_PAREN);
            ASTNode expr = parseExpression();
            consume(TokenType.RIGHT_PAREN);
            return expr;
        }
        
        throw error(peek(), "Beklenmeyen token: " + peek().type);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private boolean checkNext(TokenType type) {
        if (current + 1 >= tokens.size()) return false;
        return tokens.get(current + 1).type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return advance();
            }
        }
        String expected = Arrays.stream(types)
            .map(this::tokenTypeToString)
            .collect(Collectors.joining(" veya "));
        throw error(peek(), String.format("'%s' bekleniyordu, '%s' bulundu", expected, peek().type));
    }

    private RuntimeException error(Token token, String message) {
        return new RuntimeException("Satır " + token.line + ": " + message);
    }

    private ASTNode parseComment() {
        Token comment = consume(TokenType.COMMENT);
        return new ASTNode("Comment", comment.value);
    }

    private ASTNode parseReturn() {
        consume(TokenType.KEYWORD_GERI_VER);
        ASTNode expr = parseExpression();
        consume(TokenType.DOLLAR);
        ASTNode returnNode = new ASTNode("Return", "");
        returnNode.addChild(expr);
        return returnNode;
    }

    private String tokenTypeToString(TokenType type) {
        return switch (type) {
            case KEYWORD_TANIMLA -> "tanımla";
            case KEYWORD_TIPI -> "tür";
            case KEYWORD_BASLANGIC -> "başlangıç";
            case KEYWORD_ISE -> "ise";
            case KEYWORD_DEĞİLSE -> "değilse";
            case KEYWORD_BÖYLEYKEN -> "böyleyken";
            case KEYWORD_YAZDIR -> "yazdır";
            case KEYWORD_FONKSIYON -> "fonksiyon";
            case KEYWORD_GERI_VER -> "dön";
            case LEFT_PAREN -> "(";
            case RIGHT_PAREN -> ")";
            case LEFT_BRACE -> "{";
            case RIGHT_BRACE -> "}";
            case DOLLAR -> "$";
            case ASSIGN_OP -> "=";
            case IDENT -> "değişken adı";
            case INT_LIT -> "tamsayı";
            case STRING_LITERAL -> "metin";
            default -> type.toString();
        };
    }

    private void checkVariableExists(String name) {
        // Bu metod şu an için boş bırakılacak, çünkü değişken kontrolü Interpreter'da yapılıyor
    }

    private void checkFunctionExists(String name) {
        // Bu metod şu an için boş bırakılacak, çünkü fonksiyon kontrolü Interpreter'da yapılıyor
    }

    private void checkTypeMismatch(String expected, String found) {
        throw error(peek(), String.format("Tip uyuşmazlığı - '%s' bekleniyordu, '%s' bulundu", expected, found));
    }
}
