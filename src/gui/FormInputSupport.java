package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.border.AbstractBorder;

public final class FormInputSupport {

    private FormInputSupport() {
    }

    public static void configureEditor(JTextField textField, boolean enterMovesNext) {
        installEditingSupport(textField);
        if (enterMovesNext) {
            installEnterFocusTraversal(textField);
        }
    }

    public static void configureSearchField(JTextField searchField, Runnable onSearch, Runnable onReset) {
        installEditingSupport(searchField);

        InputMap inputMap = searchField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = searchField.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "searchNow");
        actionMap.put("searchNow", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onSearch.run();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "resetSearch");
        actionMap.put("resetSearch", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onReset.run();
            }
        });
    }

    public static void configureActionButton(AbstractButton button) {
        installRoundedButtonStyle(button);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                commitFocusedTextComponent();
            }
        });
        button.addActionListener(e -> commitFocusedTextComponent());
    }

    public static void commitFocusedTextComponent() {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner instanceof JTextComponent) {
            commitComposition((JTextComponent) focusOwner);
        }
    }

    private static void installEditingSupport(JTextField textField) {
        textField.setDragEnabled(false);
        textField.setDropTarget(null);
        textField.setTransferHandler(null);

        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                commitFocusedTextComponentExcept(textField);
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    e.consume();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    e.consume();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    e.consume();
                }
            }
        });
        textField.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Disallow mouse-drag text transfer between fields.
                e.consume();
            }
        });

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                int caretPosition = field.getCaretPosition();
                field.select(caretPosition, caretPosition);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                commitComposition(field);
                int caretPosition = field.getCaretPosition();
                field.select(caretPosition, caretPosition);
            }
        });
        textField.setBorder(new RoundedInputBorder(new Color(209, 209, 214), 12));
        textField.setBackground(new Color(255, 255, 255));
        textField.setForeground(new Color(28, 28, 30));
        textField.setCaretColor(new Color(0, 122, 255));
        textField.setSelectionColor(new Color(210, 233, 255));
        textField.setSelectedTextColor(new Color(28, 28, 30));

        UndoManager undoManager = new UndoManager();
        textField.getDocument().addUndoableEditListener(undoManager);

        InputMap inputMap = textField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textField.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("control C"), DefaultEditorKit.copyAction);
        inputMap.put(KeyStroke.getKeyStroke("control X"), DefaultEditorKit.cutAction);
        inputMap.put(KeyStroke.getKeyStroke("control V"), DefaultEditorKit.pasteAction);
        inputMap.put(KeyStroke.getKeyStroke("control A"), DefaultEditorKit.selectAllAction);

        inputMap.put(KeyStroke.getKeyStroke("control Z"), "undoChange");
        actionMap.put("undoChange", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException ignored) {
                }
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control Y"), "redoChange");
        inputMap.put(KeyStroke.getKeyStroke("control shift Z"), "redoChange");
        actionMap.put("redoChange", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotRedoException ignored) {
                }
            }
        });

        // Block X11-style paste by Shift+Insert to prevent accidental carryover.
        inputMap.put(KeyStroke.getKeyStroke("shift INSERT"), "blockShiftInsert");
        actionMap.put("blockShiftInsert", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Intentionally no-op.
            }
        });
    }

    private static void installEnterFocusTraversal(JTextComponent textComponent) {
        InputMap inputMap = textComponent.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textComponent.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "focusNextField");
        actionMap.put("focusNextField", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                commitComposition(textComponent);
                SwingUtilities.invokeLater(textComponent::transferFocus);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("shift ENTER"), "focusPreviousField");
        actionMap.put("focusPreviousField", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                commitComposition(textComponent);
                SwingUtilities.invokeLater(textComponent::transferFocusBackward);
            }
        });
    }

    private static void commitComposition(JTextComponent textComponent) {
        if (textComponent.getInputContext() != null) {
            textComponent.getInputContext().endComposition();
        }
        int caretPosition = textComponent.getCaretPosition();
        textComponent.select(caretPosition, caretPosition);
    }

    private static void commitFocusedTextComponentExcept(JTextField currentField) {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner instanceof JTextComponent && focusOwner != currentField) {
            commitComposition((JTextComponent) focusOwner);
        }
    }

    private static void installRoundedButtonStyle(AbstractButton button) {
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setBorder(new RoundedInputBorder(tone(button.getBackground(), -24), 14));

        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                int w = c.getWidth();
                int h = c.getHeight();

                Color baseColor = b.getBackground();
                Color fillColor = baseColor;
                if (model.isPressed()) {
                    fillColor = tone(baseColor, -10);
                } else if (model.isRollover()) {
                    fillColor = tone(baseColor, 12);
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);

                g2.setStroke(new BasicStroke(1f));
                g2.setColor(tone(baseColor, -24));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);
                g2.dispose();

                super.paint(g, c);
            }
        });
    }

    private static Color tone(Color color, int amount) {
        int r = clamp(color.getRed() + amount);
        int g = clamp(color.getGreen() + amount);
        int b = clamp(color.getBlue() + amount);
        return new Color(r, g, b);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static class RoundedInputBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private final Color borderColor;
        private final int radius;

        RoundedInputBorder(Color borderColor, int radius) {
            this.borderColor = borderColor;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public java.awt.Insets getBorderInsets(Component c) {
            return new java.awt.Insets(8, 12, 8, 12);
        }

        @Override
        public java.awt.Insets getBorderInsets(Component c, java.awt.Insets insets) {
            insets.left = 12;
            insets.right = 12;
            insets.top = 8;
            insets.bottom = 8;
            return insets;
        }
    }
}
