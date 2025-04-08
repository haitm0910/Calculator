package com.example.thbuoi2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thbuoi2.R;

public class MainActivity extends AppCompatActivity {

    private TextView hienthi, ketqua;
    private String expression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hienthi = findViewById(R.id.hienthi);
        ketqua = findViewById(R.id.ketqua);

        int[] numberButtonIds = new int[] {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        int[] operatorButtonIds = new int[] {
                R.id.btnPlus, R.id.btnMinus, R.id.btnMulti,
                R.id.btnDivide, R.id.btnPercent, R.id.btnDot,
                R.id.btnOpen, R.id.btnClose
        };

        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;
            expression += b.getText().toString();
            hienthi.setText(expression);
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        Button btnCA = findViewById(R.id.btnCA);
        btnCA.setOnClickListener(v -> {
            expression = "";
            hienthi.setText("");
            ketqua.setText("");
        });

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            clearLastCharacter(); // Gọi hàm xóa ký tự cuối
        });

        Button btnEqual = findViewById(R.id.btnEqual);
        btnEqual.setOnClickListener(v -> {
            try {
                double result = evaluate(expression);
                ketqua.setText(String.valueOf(result));
            } catch (Exception e) {
                ketqua.setText("Lỗi");
            }
        });
    }

    // Hàm xóa ký tự cuối
    private void clearLastCharacter() {
        if (expression.length() > 0) {
            expression = expression.substring(0, expression.length() - 1);
            hienthi.setText(expression);
        }
    }

    // Hàm tính toán đơn giản
    private double evaluate(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | number | `(` expression `)`

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else if (eat('%')) x %= parseFactor(); // modulo
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }
}