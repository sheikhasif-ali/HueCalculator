package com.huecalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MainActivity extends AppCompatActivity {
    public TextView workingTV, resultTV, resultTVtemp;

    Button zeroBtn, oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn, sixBtn, sevenBtn, eightBtn,
            nineBtn, addBtn, subtractBtn, multiplyBtn, divideBtn, powerBtn, equalBtn, clearBtn,
            parenthesesBtn, decimalBtn, backBtn;
    ImageButton themeToggle;


    String workings = "";
    String formula = "";
    String tempFormula = "";
    boolean leftBracket = true;
    boolean equals = false;
    private HashMap<Button, Integer> colorButtonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTextViews();
        setThemeIcon();

    }

    private void initTextViews() {
        colorButtonList = new HashMap<>();
        workingTV = findViewById(R.id.workingTV);
        resultTV = findViewById(R.id.resultTV);
        resultTVtemp = findViewById(R.id.resultTVtemp);
        zeroBtn = findViewById(R.id.zeroBtn);
        oneBtn = findViewById(R.id.oneBtn);
        twoBtn = findViewById(R.id.twoBtn);
        threeBtn = findViewById(R.id.threeBtn);
        fourBtn = findViewById(R.id.fourBtn);
        fiveBtn = findViewById(R.id.fiveBtn);
        sixBtn = findViewById(R.id.sixBtn);
        sevenBtn = findViewById(R.id.sevenBtn);
        eightBtn = findViewById(R.id.eightBtn);
        nineBtn = findViewById(R.id.nineBtn);
        addBtn = findViewById(R.id.addBtn);
        subtractBtn = findViewById(R.id.subtractBtn);
        multiplyBtn = findViewById(R.id.multiplyBtn);
        divideBtn = findViewById(R.id.divideBtn);
        backBtn = findViewById(R.id.backBtn);
        backBtn = findViewById(R.id.backBtn);
        powerBtn = findViewById(R.id.powersBtn);
        equalBtn = findViewById(R.id.equalBtn);
        clearBtn = findViewById(R.id.clearBtn);
        decimalBtn = findViewById(R.id.decimalBtn);
        parenthesesBtn = findViewById(R.id.parenthesesBtn);

        themeToggle = findViewById(R.id.themeToggle);

        colorButtonList.put(zeroBtn, 0);
        colorButtonList.put(oneBtn, 0);
        colorButtonList.put(twoBtn, 0);
        colorButtonList.put(threeBtn, 0);
        colorButtonList.put(fourBtn, 0);
        colorButtonList.put(fiveBtn, 0);
        colorButtonList.put(sixBtn, 0);
        colorButtonList.put(sevenBtn, 0);
        colorButtonList.put(eightBtn, 0);
        colorButtonList.put(nineBtn, 0);
        colorButtonList.put(addBtn, 0);
        colorButtonList.put(subtractBtn, 0);
        colorButtonList.put(multiplyBtn, 0);
        colorButtonList.put(divideBtn, 0);
        colorButtonList.put(equalBtn, 0);
        colorButtonList.put(parenthesesBtn, 0);
        colorButtonList.put(clearBtn, 0);
        colorButtonList.put(decimalBtn, 0);
        colorButtonList.put(powerBtn, 0);
        colorButtonList.put(backBtn, 0);


    }

    private void setWorking(String value) {
        workings += value;
        workingTV.setText(workings);
        equate(false);
    }

    public void equalsOnclick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        equals = true;
        equate(true);
        if (!resultTV.getText().toString().isEmpty()) {

            workingTV.setText("");
            workingTV.setText(resultTV.getText().toString());
            workings = resultTV.getText().toString();
        }
        int[] loc = new int[2];
        workingTV.getLocationOnScreen(loc);
        float yCord = loc[1];

        resultTVtemp.setText(resultTV.getText());
        resultTVtemp.clearAnimation();

        // Create Animator objects for each property
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(resultTVtemp, "scaleX", 1f, 0.7f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(resultTVtemp, "scaleY", 1f, 0.7f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(resultTVtemp, "alpha", 1f, 0f);
        ObjectAnimator yAnimator = ObjectAnimator.ofFloat(resultTVtemp, "y", yCord, yCord - 230);

// Create a AnimatorSet to run the animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator, yAnimator);
        animatorSet.setDuration(150);

// Add a listener to trigger the second animation after the first set completes
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator y2Animator = ObjectAnimator.ofFloat(resultTVtemp, "y", yCord - 230
                        , yCord + 200);
                ObjectAnimator alpha2Animator = ObjectAnimator.ofFloat(resultTVtemp, "alpha", 0f,
                        0f);

                AnimatorSet secondAnimatorSet = new AnimatorSet();
                secondAnimatorSet.playTogether(y2Animator, alpha2Animator);
                secondAnimatorSet.setDuration(1);
                secondAnimatorSet.start();
            }
        });

// Start the first animation set
        animatorSet.start();
        resultTV.setText("");

    }

    public void equate(boolean equalCheck) {
        if ((!workings.isEmpty())) {
            Double result = null;
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
            checkForPowerOf();

            try {
                result = (double) engine.eval(formula);
            } catch (ScriptException e) {
                if (equalCheck) {
                    Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    equals = false;

                }
            }

            if (result != null) {
                if (result - result.intValue() != 0) {
                    resultTV.setText(String.valueOf(result.doubleValue()));
                } else {
                    resultTV.setText(String.valueOf(result.intValue()));
                }
            }
        }
    }

    private void checkForPowerOf() {
        ArrayList<Integer> indexOfPowers = new ArrayList<>();
        for (int i = 0; i < workings.length(); i++) {
            if (workings.charAt(i) == '^') indexOfPowers.add(i);
        }

        formula = workings;
        tempFormula = workings;
        for (Integer index : indexOfPowers) {
            changeFormula(index);
        }
        formula = tempFormula;

        //when there is a number followed by a operator it messes up the calculation.
        //try writing 1+1 and the backspace the 1. answer should be 1 but is 2
        //this code solves it
        if (!(isNumeric(tempFormula.charAt(tempFormula.length() - 1)))) {
            formula = tempFormula.substring(0, (tempFormula.length() - 1));
        }
    }

    private void changeFormula(Integer index) {
        String numberLeft = "";
        String numberRight = "";

        for (int i = index + 1; i < workings.length(); i++) {
            if (isNumeric(workings.charAt(i))) numberRight = numberRight + workings.charAt(i);
            else break;
        }

        for (int i = index - 1; i >= 0; i--) {
            if (isNumeric(workings.charAt(i))) numberLeft = numberLeft + workings.charAt(i);
            else break;
        }

        String original = numberLeft + "^" + numberRight;
        String changed = "Math.pow(" + numberLeft + "," + numberRight + ")";
        tempFormula = tempFormula.replace(original, changed);
    }

    private boolean isNumeric(char c) {
        return (c <= '9' && c >= '0') || c == '.';
    }

    public void bracketsOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        if (leftBracket) {
            setWorking("(");
            leftBracket = false;
        } else {
            setWorking(")");
            leftBracket = true;
        }
    }

    public void clearOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        resultTV.setText("");
        workingTV.setText("");
        workings = "";
    }

    private int randomColor() {
        int minColor = Color.parseColor("#000000");
        int maxColor = Color.parseColor("#999999");

        return Color.rgb((int) (Math.random() * (Color.red(maxColor) - Color.red(minColor) + 1) + Color.red(minColor)), (int) (Math.random() * (Color.green(maxColor) - Color.green(minColor) + 1) + Color.green(minColor)), (int) (Math.random() * (Color.blue(maxColor) - Color.blue(minColor) + 1) + Color.blue(minColor)));



    }

    public void changeAllColor() {
        for (Button loop : colorButtonList.keySet()) {
            colorChanger(loop, colorButtonList.get(loop));
        }

    }

    public void colorChanger(Button button, int currentColor) {
        int newColor = randomColor();
        ObjectAnimator colorChange = ObjectAnimator.ofInt(button, "backgroundColor", currentColor
                , newColor);
        colorChange.setEvaluator(new ArgbEvaluator());
        colorChange.setDuration(800); // Duration in milliseconds
        colorChange.start();
        colorButtonList.put(button, newColor);

    }


    public void decimalOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking(".");
    }

    public void zeroOnClick(View view) {

        changeAllColor();
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS);
        //change the above code somehow so the value for the color can be changed

        setWorking("0");


    }

    public void oneOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(6);
        setWorking("1");
    }

    public void twoOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
        setWorking("2");
    }

    public void threeOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("3");
    }

    public void fourOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("4");
    }


    public void fiveOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("5");
    }

    public void sixOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("6");
    }

    public void sevenOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("7");
    }

    public void eightOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("8");
    }

    public void nineOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("9");
    }

    public void addOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("+");
    }

    public void subtractOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("-");
    }

    public void multiplyOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("*");
    }

    public void divideOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("/");
    }


    public void backOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        if (!(workings.isEmpty())) {
            String tempWorking;
            tempWorking = workings.substring(0, workings.length() - 1);
            workings = tempWorking;
            workingTV.setText(workings);

        }
        equate(false);
    }


    public void powerOnClick(View view) {
        changeAllColor();
        view.performHapticFeedback(12);
        setWorking("^");
    }

    public void toggleTheme(View view) {

        //Set the theme mode for the restarted activity.
        int darkMode = AppCompatDelegate.getDefaultNightMode();
        if(darkMode==AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        // Recreate the activity for the theme change to take effect.
        recreate();
    }

    public void setThemeIcon() {
        int darkMode = AppCompatDelegate.getDefaultNightMode();
        if(darkMode==AppCompatDelegate.MODE_NIGHT_YES) {
            themeToggle.setImageResource(R.drawable.lighttoggle);
        } else {
            themeToggle.setImageResource(R.drawable.darktoggle);
        }

    }
}