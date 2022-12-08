package com.enviogroup.fxfnsxmlparser;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class GridPaneUtils {
    /**
     * Gets row index constrain for given node, forcefully as integer: 0 as null.
     * @param node Node to look up the constraint for.
     * @return The row index as primitive integer.
     */
    public static int getRowIndexAsInteger(Node node) {
        final var a = GridPane.getRowIndex(node);
        if (a == null) {
            return 0;
        }
        return a;
    }

    /**
     * Removes row from grid pane by index.
     * Note: Might not work correctly if row spans are used.
     * @param grid Grid pane to be affected.
     * @param targetRowIndexIntegerObject Target row index to be removed. Integer object type, because for some reason `getRowIndex` returns null for children at 0th row.
     */
    public static void removeRow(GridPane grid, Integer targetRowIndexIntegerObject) {
        final int targetRowIndex = targetRowIndexIntegerObject == null ? 0 : targetRowIndexIntegerObject;

        // Remove children from row
        grid.getChildren().removeIf(node -> getRowIndexAsInteger(node) == targetRowIndex);
    }
}
