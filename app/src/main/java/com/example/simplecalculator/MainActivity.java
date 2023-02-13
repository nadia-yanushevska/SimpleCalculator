package com.example.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    // Declaring the variables:
    int lavender = Color.parseColor("#E5D9F4");
    int darkLavender = Color.parseColor("#D6B6FF");

    EditText ExpressionField;
    TextView ResultField;
    CheckBox simpleCalculator;

    String expression, memoryValue;
    boolean checked;

    int pointIndex;

    int parenthesisCount;

    Stack<Float> operands = new Stack<>();
    Stack<Character> operators = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Finding fields to be used
        ExpressionField = findViewById(R.id.editText);
        ResultField = findViewById(R.id.textView);
        simpleCalculator = findViewById(R.id.checkBox);
        // Blocking OnTouchListener
        ExpressionField.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        // Setting the field to show expression
        ExpressionField.setText(expression);

        // Initializing the values
        checked = true;
        expression = "0";
        memoryValue = "0";
        pointIndex = 0;
        parenthesisCount = 0;
    }

    public void OnNumberClick(View view) {
        Button button = (Button) view;
        // Button animation
        button.setBackgroundColor(Color.parseColor("#D8BFF8"));
        new CountDownTimer(400, 4) {

            @Override
            public void onTick(long args) {}

            @Override
            public void onFinish() {
                button.setBackgroundColor(lavender);
            }
        }.start();

        // Empty expression case
        if(expression.equals("0")) {
            if(button.getText().toString().equals(".")) {
                expression += ".";
                pointIndex = expression.length()-1;
            }
            else expression = button.getText().toString();
        }
        // Expression length check
        else if (expression.length() < 30) {
            // Point handling
            if(button.getText().toString().equals(".")) {
                if(isOperator(expression.charAt(expression.length()-1)))
                    Toast.makeText(getApplicationContext(), "Enter a number first.", Toast.LENGTH_SHORT).show();
                else if(pointIndex > 0)
                    Toast.makeText(getApplicationContext(), "Number already has a decimal point.", Toast.LENGTH_SHORT).show();
                else if(expression.length() == 29)
                    Toast.makeText(getApplicationContext(), "Maximum number of digits reached.", Toast.LENGTH_SHORT).show();
                else {
                    expression += ".";
                    pointIndex = expression.length()-1;
                }
            }
            else {
                // Case last number entered without decimal point
                if(pointIndex == 0) {
                    expression += button.getText().toString();
                    // Divide by zero case
                    if(divideByZero()) {
                        Toast.makeText(getApplicationContext(), "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                        if(expression.length() == 29)
                            expression = expression.substring(0, expression.length()-1);
                        else {
                            expression += ".";
                            pointIndex = expression.length()-1;
                        }
                    }
                }
                // Number of digits after decimal point check
                else if(expression.length() - pointIndex <= 5)
                    expression += button.getText().toString();
                else Toast.makeText(getApplicationContext(), "Maximum number of digits after decimal point reached.", Toast.LENGTH_SHORT).show();
            }
        }
        else Toast.makeText(getApplicationContext(), "Maximum number of digits reached.", Toast.LENGTH_SHORT).show();

        // Setting fields to show expression and intermediate result
        ExpressionField.setText(expression);
        if(simpleCalculator.isChecked())
            ResultField.setText(evaluator());
        else ResultField.setText(expressionEvaluator());
    }

    public void OnSignClick(View view) {
        Button button = (Button) view;
        // Button animation
        button.setBackgroundColor(Color.parseColor("#D8BFF8"));
        new CountDownTimer(400, 4) {

            @Override
            public void onTick(long args) {}

            @Override
            public void onFinish() {
                button.setBackgroundColor(lavender);
            }
        }.start();

        // Empty expression case
        if(expression.equals("0"))
            expression = "-";
        // Case last entered was operator
        else if(isOperator(expression.charAt(expression.length()-1))) {
            switch (expression.charAt(expression.length() - 1)) {
                case '+':
                    expression = expression.substring(0, expression.length() - 1);
                    expression += "-";
                    break;
                case '-':
                    expression = expression.substring(0, expression.length() - 1);
                    expression += "+";
                    break;
                case '*':
                case '/':
                    // Expression length check
                    if (expression.length() < 28) {
                        expression += "(-";
                        parenthesisCount++;
                    }
                    else Toast.makeText(getApplicationContext(), "Maximum number of digits reached.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        else Toast.makeText(getApplicationContext(), "Enter a negative sign before the number, please.", Toast.LENGTH_SHORT).show();

        // Setting fields to show expression and intermediate result
        ExpressionField.setText(expression);
        if(simpleCalculator.isChecked())
            ResultField.setText(evaluator());
        else ResultField.setText(expressionEvaluator());
    }

    public void OnOperatorClick(View view) {
        Button button = (Button) view;
        // Button animation
        button.setBackgroundColor(Color.parseColor("#D8BFF8"));
        new CountDownTimer(400, 4) {

            @Override
            public void onTick(long args) {}

            @Override
            public void onFinish() {
                button.setBackgroundColor(lavender);
            }
        }.start();

        // Case last entered is operator or a decimal point
        if(isOperator(expression.charAt(expression.length()-1)) || expression.charAt(expression.length()-1) == '.')
            Toast.makeText(getApplicationContext(), "Enter a number first.", Toast.LENGTH_SHORT).show();
        // Empty expression case
        else if(expression.equals("0"))
            expression += button.getText().toString();
        // Expression length check
        else if(expression.length() < 29) {
            pointIndex = 0;
            // Closing parenthesis
            if(parenthesisCount > 0) {
                expression += ")";
                parenthesisCount--;
            }
            expression += button.getText().toString();
        }
        else Toast.makeText(getApplicationContext(), "Maximum number of digits reached.", Toast.LENGTH_SHORT).show();

        // Setting fields to show expression and intermediate result
        ExpressionField.setText(expression);
        if(simpleCalculator.isChecked())
            ResultField.setText(evaluator());
        else ResultField.setText(expressionEvaluator());
    }

    public void OnEqualsClick(View view) {
        Button button = (Button) view;
        // Button animation
        button.setBackgroundColor(Color.parseColor("#C092FB"));
        new CountDownTimer(400, 4) {

            @Override
            public void onTick(long args) {}

            @Override
            public void onFinish() {
                button.setBackgroundColor(darkLavender);
            }
        }.start();

        // Divide by zero case
        if(divideByZeroPoint())
            Toast.makeText(getApplicationContext(), "Cannot divide by zero. Enter a number first.", Toast.LENGTH_SHORT).show();
        else {
            // Evaluating
            if(simpleCalculator.isChecked())
                expression = evaluator();
            else expression = expressionEvaluator();

            // Updating the memoryValue and the fields
            memoryValue = expression;
            ExpressionField.setText(expression);
            ResultField.setText(expression);
            // Updating other variables
            parenthesisCount = 0;
            if (expression.contains("."))
                pointIndex = expression.indexOf(".");
            else pointIndex = 0;
        }
    }

    public void OnEraserClick(View view) {
        Button button = (Button) view;
        // Button animation
        button.setBackgroundColor(Color.parseColor("#C092FB"));
        new CountDownTimer(400, 4) {

            @Override
            public void onTick(long args) {}

            @Override
            public void onFinish() {
                button.setBackgroundColor(lavender);
            }
        }.start();

        // Clear button clicked
        if (button.getText().toString().equals("C") || expression.length() == 1) {
            expression = "0";
            ExpressionField.setText(expression);
            ResultField.setText("0");
            parenthesisCount = 0;
            pointIndex = 0;
        }
        // Backspace button clicked
        else {
            // Remove last entered digit
            char temp = expression.charAt(expression.length()-1);
            expression = expression.substring(0, expression.length()-1);

            // Handling different cases
            if(temp == ')')
                parenthesisCount++;
            else if(temp == '-' && expression.charAt(expression.length()-1) == '(') {
                expression = expression.substring(0, expression.length()-1);
                parenthesisCount--;
            }
            else if(divideByZero() && temp == '.') {
                Toast.makeText(getApplicationContext(), "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                expression = expression.substring(0, expression.length() - 1);
                pointIndex = 0;
            }
            else if(temp == '.')
                pointIndex = 0;

            // Setting fields to show expression and intermediate result
            ExpressionField.setText(expression);
            if(simpleCalculator.isChecked())
                ResultField.setText(evaluator());
            else ResultField.setText(expressionEvaluator());
        }
    }

    public void OnMemoryClick(View view) {
        Button button = (Button) view;
        // Button animation
        button.setBackgroundColor(Color.parseColor("#C092FB"));
        new CountDownTimer(400, 4) {

            @Override
            public void onTick(long args) {}

            @Override
            public void onFinish() {
                button.setBackgroundColor(lavender);
            }
        }.start();

        // Memory clear button clicked
        if (button.getText().toString().equals("MC"))
            memoryValue = "0";
        // Memory recall button clicked
        else if (button.getText().toString().equals("MR")) {
            Button btn = findViewById(R.id.button1);
            btn.performClick();
            expression = memoryValue;
            ExpressionField.setText(expression);
            ResultField.setText(memoryValue);
        }
        // Memory add or subtract buttons clicked
        else {
            if (ExpressionField.getText().toString().equals("0")) {
                expression = memoryValue + button.getText().toString().charAt(1);
                ExpressionField.setText(expression);
                ResultField.setText(memoryValue);
            }
            else if(expression.length() + memoryValue.length() + 1 < 30) {
                if(isOperator(expression.charAt(expression.length()-1)))  Toast.makeText(getApplicationContext(), "Enter a number first.", Toast.LENGTH_SHORT).show();
                else {
                    expression += button.getText().toString().charAt(1) + memoryValue;
                    ExpressionField.setText(expression);
                    if(simpleCalculator.isChecked())
                        ResultField.setText(evaluator());
                    else ResultField.setText(expressionEvaluator());
                }
            }
            else Toast.makeText(getApplicationContext(), "Maximum number of digits reached.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public boolean divideByZero() {
        return expression.charAt(expression.length()-1) == '0' && expression.charAt(expression.length()-2) == '/';
    }

    public boolean divideByZeroPoint() {
        // Point entered by division by zeroscenario handling
        return expression.charAt(expression.length()-2) == '0' && expression.charAt(expression.length()-3) == '/' && expression.charAt(expression.length()-1) == '.';
    }

    public float Multiplicator(float mult) {
        // Operands empty case
        if (operands.isEmpty()) return mult;

        // Declaring and initializing the values
        char opr;
        if (operators.isEmpty())  opr = '+';
        else opr = operators.pop();

        float opn = operands.pop();

        // Evaluating the mult value
        switch (opr) {
            case '+':
                mult *= opn;
                break;
            case '-':
                mult = -mult*opn;
                break;
            case'*':
                return Multiplicator(mult*opn);
            case'/':
                return Multiplicator(mult/opn);
        }

        return mult;
    }

    public String expressionEvaluator() {
        // Declaring the variables
        float operand;
        boolean isSigned = false, hasPoint = false;
        int numberCount = 0, pointCount = 0;
        String result;

        // Empty expression case
        if(expression.equals("0"))
            return expression;

        for(int i = 0; i < expression.length(); i++) {
            // Brackets cases
            if(expression.charAt(i) == '(') {
                if(i != expression.length()-2) {
                    i += 2;
                    isSigned = true;
                }
                else break;
            }
            else if(expression.charAt(i) == ')') {
                if(i != expression.length()-1) {
                    i++;
                    isSigned = false;
                }
                else break;
            }
            // Point check
            else if(expression.charAt(i) == '.') {
                hasPoint = true;
                i++;
            }

            // Operator case
            if(isOperator(expression.charAt(i))) {
                operators.push(expression.charAt(i));
                // Last operand has decimal point case
                if(hasPoint)
                    operands.push((float) (operands.pop() / Math.pow(10, pointCount)));
                isSigned = false;
                pointCount = 0;
                hasPoint = false;
                numberCount=0;
            }
            // Number case
            else {
                int opd = expression.charAt(i)-48;
                if(isSigned) opd = -opd;
                if (operands.isEmpty() || numberCount==0) operands.push((float)opd);
                else {
                    operand = operands.pop();
                    operand = operand * 10 + opd;
                    operands.push(operand);
                    if(hasPoint) pointCount++;
                }
                numberCount++;
            }
            // Ignoring last entered if it is an operator or a decimal point
            if(i == expression.length()-2 && (isOperator(expression.charAt(expression.length()-1)) || expression.charAt(expression.length()-1) == '.'))
                break;
        }
        // Last operand has decimal point case
        if(hasPoint)
            operands.push((float) (operands.pop() / Math.pow(10, pointCount)));

        // Evaluating the expression
        float sum=0;
        while(!operands.isEmpty()) {
            sum+=Multiplicator(1);
        }
        // Sum equals zero case
        if(sum == 0)
            return "0";

        // Formatting the sum and result
        DecimalFormat df = new DecimalFormat("#.#####");
        result = df.format(sum);
        if(result.contains(".")) {
            while (result.charAt(result.length() - 1) == '0') {
                result = result.substring(0, result.length() - 1);
            }
            if (result.charAt(result.length() - 1) == '.') {
                result = result.substring(0, result.length() - 1);
            }
        }

        // Clearing the stacks
        operands.clear();
        operators.clear();

        return result;
    }

    public float operate(float num1, float num2, char opr) {
        float result = 0;

        switch (opr) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case '*':
                result = num1 * num2;
                break;
            case '/':
                result = num1 / num2;
                break;
        }

        return result;
    }

    public String evaluator() {
        // Declaring the variables
        float opd = 0, sum = 0;
        char opr = ' ';
        boolean isSigned = false, hasPoint = false;
        int numCount = 0, pointCount = 0;
        String result;

        // Empty expression case
        if(expression.equals("0"))
            return "0";

        for(int i = 0; i < expression.length(); i++) {
            // Brackets cases
            if(expression.charAt(i) == '(') {
                if(i != expression.length()-2) {
                    i += 2;
                    isSigned = true;
                }
                else break;
            }
            else if(expression.charAt(i) == ')') {
                if(i != expression.length()-1) {
                    i++;
                    isSigned = false;
                }
                else break;
            }
            // Point check
            else if(expression.charAt(i) == '.') {
                hasPoint = true;
                i++;
            }

            // Operator case
            if(isOperator(expression.charAt(i))) {
                if(hasPoint) opd = (float) (opd / Math.pow(10, pointCount));
                if(opr == ' ')
                    sum = opd;
                else sum = operate(sum, opd, opr);
                numCount = 0;
                pointCount = 0;
                hasPoint = false;
                isSigned = false;
                opr = expression.charAt(i);
            }
            // Number case
            else {
                int num = expression.charAt(i) - 48;
                if(isSigned) num = -num;
                if(hasPoint) pointCount++;
                if(numCount == 0) {
                    opd = num;
                    numCount++;
                }
                else {
                    opd = opd*10 + num;
                    numCount++;
                }
            }
            // Ignoring last entered if it is an operator or a decimal point
            if(i == expression.length()-2 && (isOperator(expression.charAt(expression.length()-1)) || expression.charAt(expression.length()-1) == '.'))
                break;
        }
        // Last operand has decimal point
        if(hasPoint) opd = (float) (opd / Math.pow(10, pointCount));

        // Last evaluation
        if(opr == ' ')
            opr = '+';
        sum = operate(sum, opd, opr);

        // Formatting the sum and result
        DecimalFormat df = new DecimalFormat("#.#####");
        result = df.format(sum);
        if(result.contains(".")) {
            while (result.charAt(result.length() - 1) == '0') {
                result = result.substring(0, result.length() - 1);
            }
            if (result.charAt(result.length() - 1) == '.') {
                result = result.substring(0, result.length() - 1);
            }
        }

        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Saving the values
        outState.putString("expression", expression);
        outState.putString("memoryValue", memoryValue);
        outState.putInt("parenthesisCount", parenthesisCount);
        outState.putInt("pointIndex", pointIndex);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        // Restoring the values
        expression = outState.getString("expression");
        memoryValue = outState.getString("memoryValue");
        parenthesisCount = outState.getInt("parenthesisCount");
        pointIndex = outState.getInt("pointIndex");

        ExpressionField.setText(expression);
        ResultField.setText(expressionEvaluator());
    }
}