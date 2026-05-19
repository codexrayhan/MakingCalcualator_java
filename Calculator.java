import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;

/**
 * Calculator.java
 * A modern, eye-soothing Java Swing Calculator with CSS-inspired styling.
 * Supports: Addition, Subtraction, Multiplication, Division,
 *           Percentage, Square Root, Toggle Sign, and Clear functions.
 *
 * Design Theme: Deep space dark with soft teal/cyan accent palette.
 */
public class Calculator extends JFrame {

    // ─── Color Palette (CSS-inspired variables) ───────────────────────────────
    private static final Color BG_DARK        = new Color(15, 17, 26);       // #0F111A
    private static final Color PANEL_BG       = new Color(22, 25, 38);       // #161926
    private static final Color DISPLAY_BG     = new Color(10, 12, 20);       // #0A0C14
    private static final Color BTN_NUMBER     = new Color(35, 40, 60);       // #23283C
    private static final Color BTN_OPERATOR   = new Color(0, 180, 160);      // #00B4A0 teal
    private static final Color BTN_SPECIAL    = new Color(50, 55, 80);       // #323750
    private static final Color BTN_EQUALS     = new Color(0, 210, 180);      // #00D2B4 bright teal
    private static final Color BTN_CLEAR      = new Color(220, 70, 90);      // #DC465A red
    private static final Color BTN_HOVER_NUM  = new Color(55, 62, 90);       // lighter on hover
    private static final Color TEXT_PRIMARY   = new Color(230, 235, 255);    // #E6EBFF
    private static final Color TEXT_SECONDARY = new Color(130, 145, 185);    // #8291B9
    private static final Color TEXT_OPERATOR  = new Color(255, 255, 255);    // white on teal
    private static final Color SHADOW_COLOR   = new Color(0, 0, 0, 120);

    // ─── Font Definitions ─────────────────────────────────────────────────────
    private static final Font DISPLAY_FONT    = new Font("SansSerif", Font.PLAIN, 46);
    private static final Font DISPLAY_SMALL   = new Font("SansSerif", Font.PLAIN, 22);
    private static final Font BUTTON_FONT     = new Font("SansSerif", Font.BOLD, 20);
    private static final Font BUTTON_FONT_SM  = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font TITLE_FONT      = new Font("SansSerif", Font.BOLD, 13);

    // ─── State Variables ──────────────────────────────────────────────────────
    private double firstOperand  = 0;
    private double secondOperand = 0;
    private String operator      = "";
    private boolean newInput     = true;
    private boolean calculated   = false;

    // ─── UI Components ────────────────────────────────────────────────────────
    private JLabel expressionLabel;   // shows "12 + " (history line)
    private JLabel displayLabel;      // main big number display

    // ──────────────────────────────────────────────────────────────────────────

    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 660);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // ── Top title bar ──
        JPanel titleBar = buildTitleBar();
        add(titleBar, BorderLayout.NORTH);

        // ── Main container ──
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(new EmptyBorder(0, 16, 16, 16));

        // ── Display panel ──
        JPanel displayPanel = buildDisplayPanel();
        mainPanel.add(displayPanel, BorderLayout.NORTH);

        // ── Button grid ──
        JPanel buttonGrid = buildButtonGrid();
        mainPanel.add(buttonGrid, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    // ─── Title Bar ────────────────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL_BG);
        bar.setPreferredSize(new Dimension(400, 44));
        bar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("CALCULATOR");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_SECONDARY);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Decorative teal dot
        JLabel dot = new JLabel("◈");
        dot.setFont(new Font("SansSerif", Font.BOLD, 16));
        dot.setForeground(BTN_OPERATOR);

        bar.add(dot, BorderLayout.WEST);
        bar.add(title, BorderLayout.CENTER);

        // Bottom separator line
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(40, 45, 65)),
            new EmptyBorder(0, 20, 0, 20)
        ));
        return bar;
    }

    // ─── Display Panel ────────────────────────────────────────────────────────
    private JPanel buildDisplayPanel() {
        JPanel panel = new RoundedPanel(18, DISPLAY_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 22, 18, 22));
        panel.setPreferredSize(new Dimension(368, 130));

        // Expression (history) label
        expressionLabel = new JLabel(" ");
        expressionLabel.setFont(DISPLAY_SMALL);
        expressionLabel.setForeground(TEXT_SECONDARY);
        expressionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Main number display
        displayLabel = new JLabel("0");
        displayLabel.setFont(DISPLAY_FONT);
        displayLabel.setForeground(TEXT_PRIMARY);
        displayLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(expressionLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(displayLabel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);
        wrapper.setBorder(new EmptyBorder(12, 0, 14, 0));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    // ─── Button Grid ──────────────────────────────────────────────────────────
    private JPanel buildButtonGrid() {
        JPanel grid = new JPanel(new GridLayout(5, 4, 10, 10));
        grid.setBackground(BG_DARK);

        // Row 1: AC  +/-  %  ÷
        grid.add(makeButton("AC",  BTN_CLEAR,    TEXT_PRIMARY,  BUTTON_FONT,    "clear"));
        grid.add(makeButton("+/-", BTN_SPECIAL,  TEXT_PRIMARY,  BUTTON_FONT_SM, "toggle"));
        grid.add(makeButton("%",   BTN_SPECIAL,  TEXT_PRIMARY,  BUTTON_FONT,    "percent"));
        grid.add(makeButton("÷",   BTN_OPERATOR, TEXT_OPERATOR, BUTTON_FONT,    "/"));

        // Row 2: 7  8  9  ×
        grid.add(makeButton("7", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "7"));
        grid.add(makeButton("8", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "8"));
        grid.add(makeButton("9", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "9"));
        grid.add(makeButton("×", BTN_OPERATOR, TEXT_OPERATOR, BUTTON_FONT, "*"));

        // Row 3: 4  5  6  −
        grid.add(makeButton("4", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "4"));
        grid.add(makeButton("5", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "5"));
        grid.add(makeButton("6", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "6"));
        grid.add(makeButton("−", BTN_OPERATOR, TEXT_OPERATOR, BUTTON_FONT, "-"));

        // Row 4: 1  2  3  +
        grid.add(makeButton("1", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "1"));
        grid.add(makeButton("2", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "2"));
        grid.add(makeButton("3", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "3"));
        grid.add(makeButton("+", BTN_OPERATOR, TEXT_OPERATOR, BUTTON_FONT, "+"));

        // Row 5: √  0  .  =
        grid.add(makeButton("√", BTN_SPECIAL, TEXT_PRIMARY,  BUTTON_FONT_SM, "sqrt"));
        grid.add(makeButton("0", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "0"));
        grid.add(makeButton(".", BTN_NUMBER, TEXT_PRIMARY,  BUTTON_FONT, "."));
        grid.add(makeButton("=", BTN_EQUALS, TEXT_OPERATOR, BUTTON_FONT, "="));

        return grid;
    }

    // ─── Button Factory ───────────────────────────────────────────────────────
    private JButton makeButton(String label, Color bg, Color fg, Font font, String action) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(SHADOW_COLOR);
                g2.fill(new RoundRectangle2D.Float(3, 4, getWidth()-3, getHeight()-3, 16, 16));
                // Button face
                Color face = getModel().isPressed()
                        ? bg.darker()
                        : (getModel().isRollover() ? bg.brighter() : bg);
                g2.setColor(face);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-3, getHeight()-4, 16, 16));
                // Subtle top-edge highlight
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRect(4, 1, getWidth()-10, 6);
                g2.dispose();
                // Text (drawn by default but we override bg)
                setForeground(fg);
                super.paintComponent(g);
            }
        };

        btn.setFont(font);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 70));

        btn.addActionListener(e -> handleAction(action));

        // Hover animation via mouse listener
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { btn.repaint(); }
        });

        return btn;
    }

    // ─── Logic Handler ────────────────────────────────────────────────────────
    private void handleAction(String action) {
        String current = displayLabel.getText();

        switch (action) {
            case "clear":
                firstOperand  = 0;
                secondOperand = 0;
                operator      = "";
                newInput      = true;
                calculated    = false;
                displayLabel.setText("0");
                expressionLabel.setText(" ");
                break;

            case "toggle":
                if (!current.equals("0")) {
                    double val = Double.parseDouble(current);
                    val = -val;
                    displayLabel.setText(formatResult(val));
                }
                break;

            case "percent":
                double pval = Double.parseDouble(current);
                pval = pval / 100.0;
                displayLabel.setText(formatResult(pval));
                newInput = true;
                break;

            case "sqrt":
                double sqrtVal = Double.parseDouble(current);
                if (sqrtVal < 0) {
                    displayLabel.setText("Error");
                    expressionLabel.setText(" ");
                } else {
                    expressionLabel.setText("√(" + formatResult(sqrtVal) + ")");
                    displayLabel.setText(formatResult(Math.sqrt(sqrtVal)));
                }
                newInput = true;
                calculated = true;
                break;

            case "+":
            case "-":
            case "*":
            case "/":
                if (!operator.isEmpty() && !newInput) {
                    // Chain calculation
                    secondOperand = Double.parseDouble(current);
                    double chainResult = compute(firstOperand, secondOperand, operator);
                    expressionLabel.setText(formatResult(firstOperand)
                            + " " + operatorSymbol(operator)
                            + " " + formatResult(secondOperand)
                            + " " + operatorSymbol(action));
                    firstOperand = chainResult;
                    displayLabel.setText(formatResult(chainResult));
                } else {
                    firstOperand = Double.parseDouble(current);
                    expressionLabel.setText(formatResult(firstOperand)
                            + " " + operatorSymbol(action));
                }
                operator = action;
                newInput = true;
                calculated = false;
                break;

            case "=":
                if (!operator.isEmpty()) {
                    secondOperand = Double.parseDouble(current);
                    expressionLabel.setText(formatResult(firstOperand)
                            + " " + operatorSymbol(operator)
                            + " " + formatResult(secondOperand) + " =");
                    double result = compute(firstOperand, secondOperand, operator);
                    if (Double.isInfinite(result)) {
                        displayLabel.setText("Error");
                    } else {
                        displayLabel.setText(formatResult(result));
                    }
                    firstOperand = result;
                    operator = "";
                    newInput = true;
                    calculated = true;
                }
                break;

            case ".":
                if (newInput) {
                    displayLabel.setText("0.");
                    newInput = false;
                } else if (!current.contains(".")) {
                    displayLabel.setText(current + ".");
                }
                break;

            default: // digit pressed
                if (newInput || calculated) {
                    displayLabel.setText(action);
                    newInput = false;
                    calculated = false;
                } else {
                    if (current.equals("0")) {
                        displayLabel.setText(action);
                    } else {
                        if (current.length() < 12) {
                            displayLabel.setText(current + action);
                        }
                    }
                }
                // Auto-shrink font for long numbers
                adjustDisplayFont(displayLabel.getText());
                break;
        }

        adjustDisplayFont(displayLabel.getText());
    }

    // ─── Arithmetic ───────────────────────────────────────────────────────────
    private double compute(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return b == 0 ? Double.POSITIVE_INFINITY : a / b;
            default:  return b;
        }
    }

    private String operatorSymbol(String op) {
        switch (op) {
            case "+": return "+";
            case "-": return "−";
            case "*": return "×";
            case "/": return "÷";
            default:  return op;
        }
    }

    // ─── Number Formatting ────────────────────────────────────────────────────
    private String formatResult(double val) {
        if (val == Math.floor(val) && !Double.isInfinite(val) && Math.abs(val) < 1e12) {
            return String.valueOf((long) val);
        }
        // Trim trailing zeros
        String s = String.format("%.8f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    // ─── Responsive Font Size ─────────────────────────────────────────────────
    private void adjustDisplayFont(String text) {
        if (text.length() > 10) {
            displayLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));
        } else if (text.length() > 7) {
            displayLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        } else {
            displayLabel.setFont(DISPLAY_FONT);
        }
    }

    // ─── Rounded Panel Helper ─────────────────────────────────────────────────
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
