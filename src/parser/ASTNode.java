// === src/parser/ASTNode.java ===
package parser;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    public final String type;
    public final String value;
    public final List<ASTNode> children;

    public ASTNode(String type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
        this.children.add(child);
    }

    public String toString() {
        return type + "(" + value + ")";
    }
}
