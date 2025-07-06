// === src/interpreter/Interpreter.java ===
package interpreter;

import java.util.*;
import parser.ASTNode;

public class Interpreter {
    private final Deque<Map<String, Object>> scopes = new ArrayDeque<>();
    private final Map<String, Function> functions = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public Interpreter() {
        scopes.push(new HashMap<>()); // global scope
    }

    public void execute(ASTNode node) {
        switch (node.type) {
            case "Program" -> {
                for (ASTNode child : node.children) {
                    execute(child);
                }
            }
            case "VarDecl" -> {
                String name = node.value;
                String type = (String) evaluate(node.children.get(0));
                Object value = evaluate(node.children.get(1));
                define(name, value);
            }
            case "Print" -> {
                Object value = evaluate(node.children.get(0));
                System.out.println(value);
            }
            case "If" -> {
                Object condition = evaluate(node.children.get(0));
                if (toBoolean(condition)) {
                    execute(node.children.get(1));
                } else if (node.children.size() > 2) {
                    execute(node.children.get(2));
                }
            }
            case "While" -> executeWhile(node);
            case "Function" -> {
                String name = node.value;
                List<String> params = new ArrayList<>();
                for (int i = 0; i < node.children.size() - 1; i++) {
                    params.add(node.children.get(i).value);
                }
                ASTNode body = node.children.get(node.children.size() - 1);
                functions.put(name, new Function(params, body));
            }
            case "Return" -> {
                Object value = evaluate(node.children.get(0));
                throw new ReturnValue(value);
            }
            case "Block" -> {
                scopes.push(new HashMap<>());
                for (ASTNode stmt : node.children) {
                    execute(stmt);
                }
                scopes.pop();
            }
            case "Call" -> evaluateCall(node);
            case "Comment" -> {} // Yorum satırlarını görmezden gel
            case "BinOp" -> evaluate(node); // BinOp düğümlerini evaluate et
            default -> throw new RuntimeException("Bilinmeyen düğüm tipi: " + node.type);
        }
    }

    private void executeWhile(ASTNode node) {
        while (toBoolean(evaluate(node.children.get(0)))) {
            execute(node.children.get(1));
        }
    }

    private static class Function {
        final List<String> params;
        final ASTNode body;

        Function(List<String> params, ASTNode body) {
            this.params = params;
            this.body = body;
        }
    }

    private static class ReturnValue extends RuntimeException {
        private static final long serialVersionUID = 1L;
        @SuppressWarnings("serial")
        final transient Object value;

        ReturnValue(Object value) {
            this.value = value;
        }
    }

    private Object evaluate(ASTNode node) {
        return switch (node.type) {
            case "IntLit" -> Integer.parseInt(node.value);
            case "FloatLit" -> Double.parseDouble(node.value);
            case "StringLit" -> node.value.replaceAll("\"", "");
            case "BooleanLit" -> node.value.equals("doğru");
            case "Var" -> lookup(node.value);
            case "BinOp" -> evaluateBinaryOp(node);
            case "Block" -> evaluateBlock(node);
            case "Call" -> evaluateCall(node);
            case "DataType" -> node.value;
            case "InputPrompt" -> {
                System.out.print(node.value + ": ");
                yield scanner.nextLine();
            }
            default -> throw new RuntimeException("Geçersiz ifade: " + node.type);
        };
    }

    private Object evaluateCall(ASTNode node) {
        String name = node.value;
        List<Object> args = new ArrayList<>();
        
        for (ASTNode arg : node.children) {
            args.add(evaluate(arg));
        }
        
        if (name.equals("yazdır")) {
            System.out.println(args.get(0));
            return null;
        }
        
        Function function = functions.get(name);
        if (function == null) {
            throw new RuntimeException("Tanımlanmamış fonksiyon: " + name);
        }
        
        if (args.size() != function.params.size()) {
            throw new RuntimeException("Fonksiyon " + name + " " + function.params.size() +
                    " parametre bekliyor, ancak " + args.size() + " parametre verildi");
        }
        
        // Yeni bir scope oluştur
        scopes.push(new HashMap<>());
        
        // Parametreleri bağla
        for (int i = 0; i < args.size(); i++) {
            define(function.params.get(i), args.get(i));
        }
        
        // Fonksiyon gövdesini çalıştır
        Object result = null;
        try {
            execute(function.body);
            return null;
        } catch (ReturnValue returnValue) {
            result = returnValue.value;
        } finally {
            scopes.pop();
        }
        
        return result;
    }

    private Object evaluateBinaryOp(ASTNode node) {
        if (node.value.equals("=")) {
            String name = node.children.get(0).value;
            Object value = evaluate(node.children.get(1));
            assign(name, value);
            return value;
        }

        Object left = evaluate(node.children.get(0));
        Object right = evaluate(node.children.get(1));
        return switch (node.value) {
            case "+" -> {
                if (left instanceof String || right instanceof String) {
                    yield String.valueOf(left) + String.valueOf(right);
                }
                yield toNumber(left) + toNumber(right);
            }
            case "-" -> toNumber(left) - toNumber(right);
            case "*" -> toNumber(left) * toNumber(right);
            case "/" -> {
                double r = toNumber(right);
                if (r == 0) throw new ArithmeticException("Sıfıra bölme hatası!");
                yield toNumber(left) / r;
            }
            case "%" -> {
                double r = toNumber(right);
                if (r == 0) throw new ArithmeticException("Sıfıra bölme hatası!");
                yield toNumber(left) % r;
            }
            case "==" -> left.equals(right);
            case ">" -> toNumber(left) > toNumber(right);
            case "<" -> toNumber(left) < toNumber(right);
            case ">=" -> toNumber(left) >= toNumber(right);
            case "<=" -> toNumber(left) <= toNumber(right);
            case "ve" -> toBoolean(left) && toBoolean(right);
            case "veya" -> toBoolean(left) || toBoolean(right);
            default -> throw new RuntimeException("Bilinmeyen işlem: " + node.value);
        };
    }

    private void define(String name, Object value) {
        scopes.peek().put(name, value);
    }

    private void assign(String name, Object value) {
        for (Map<String, Object> scope : scopes) {
            if (scope.containsKey(name)) {
                scope.put(name, value);
                return;
            }
        }
        throw new RuntimeException("Tanımsız değişken: " + name);
    }

    private Object lookup(String name) {
        for (Map<String, Object> scope : scopes) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        throw new RuntimeException("Tanımsız değişken: " + name);
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) return !((String) value).isEmpty();
        if (value == null) return false;
        return true;
    }

    private double toNumber(Object obj) {
        if (obj instanceof Integer i) return i;
        if (obj instanceof Double d) return d;
        throw new RuntimeException("Sayısal olmayan değer: " + obj);
    }

    private Object evaluateBlock(ASTNode block) {
        scopes.push(new HashMap<>());
        Object last = null;
        for (ASTNode stmt : block.children) {
            last = evaluate(stmt);
        }
        scopes.pop();
        return last;
    }

    private void error(String message) {
        throw new RuntimeException("ÇALIŞMA ZAMANI HATASI: " + message);
    }

    private void checkType(Object value, String expectedType) {
        String actualType = getType(value);
        if (!actualType.equals(expectedType)) {
            error(String.format("Tip uyuşmazlığı: '%s' tipinde değer bekleniyordu, '%s' tipinde değer bulundu", 
                expectedType, actualType));
        }
    }

    private String getType(Object value) {
        if (value instanceof Integer) return "tamsayı";
        if (value instanceof Double) return "ondalıklı";
        if (value instanceof String) return "metin";
        if (value instanceof Boolean) return "mantıksal";
        return "bilinmeyen";
    }

    private void checkDivisionByZero(Object divisor) {
        if (divisor instanceof Number && ((Number)divisor).doubleValue() == 0) {
            error("Sıfıra bölme hatası");
        }
    }

    private void checkVariableExists(String name) {
        boolean exists = false;
        for (Map<String, Object> scope : scopes) {
            if (scope.containsKey(name)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            error(String.format("'%s' değişkeni tanımlanmamış", name));
        }
    }

    private void checkFunctionExists(String name) {
        if (!functions.containsKey(name)) {
            error(String.format("'%s' fonksiyonu tanımlanmamış", name));
        }
    }

    private void checkArgumentCount(String funcName, int expected, int actual) {
        if (expected != actual) {
            error(String.format("'%s' fonksiyonu %d parametre bekliyor, %d parametre verildi", 
                funcName, expected, actual));
        }
    }
}
