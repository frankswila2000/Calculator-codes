import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Cal extends JFrame {
    private JTextField inputField;

    public Cal() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);

        // Create input field
        inputField = new JTextField(10);
        inputField.setEditable(true);
        inputField.setHorizontalAlignment(SwingConstants.RIGHT);
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));

        // Create buttons
        JPanel buttonPanel = new JPanel(new GridLayout(4, 4));
        String[] buttonLabels = {"7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+", "DEL"}; // Added "DEL" button
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        // Add components to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputField, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.CENTER);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton) e.getSource()).getText();
            if (command.equals("=")) {
                try {
                    String expression = inputField.getText();
                    double result = evaluateExpression(expression);
                    inputField.setText(String.valueOf(result));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Cal.this, "Invalid expression", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (command.equals("DEL")) { // Handling delete button
                String currentText = inputField.getText();
                if (!currentText.isEmpty()) {
                    inputField.setText(currentText.substring(0, currentText.length() - 1));
                }
            } else {
                inputField.setText(inputField.getText() + command);
            }
        }
    }

    private double evaluateExpression(String expression) {
        return Calculator.evaluate(expression);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Cal calculator = new Cal();
            calculator.setVisible(true);
        });
    }
}

class Calculator {
    public static double evaluate(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean isDigit(char c) {
                return Character.isDigit(c) || c == '.';
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if (isDigit((char) ch)) {
                    while (isDigit((char) ch)) nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }

            boolean eat(int charToEat) {
                while (Character.isWhitespace(ch)) nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
        }.parse();
    }
}
