package com.huecalculator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.huecalculator.databinding.ActivityMainBinding
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    var workingTV: TextView? = null
//    var resultTV: TextView? = null
//    var resultTVtemp: TextView? = null
//
//    var zeroBtn: Button? = null
//    var oneBtn: Button? = null
//    var twoBtn: Button? = null
//    var threeBtn: Button? = null
//    var fourBtn: Button? = null
//    var fiveBtn: Button? = null
//    var sixBtn: Button? = null
//    var sevenBtn: Button? = null
//    var eightBtn: Button? = null
//    var nineBtn: Button? = null
//    var addBtn: Button? = null
//    var subtractBtn: Button? = null
//    var multiplyBtn: Button? = null
//    var divideBtn: Button? = null
//    var powerBtn: Button? = null
//    var equalBtn: Button? = null
//    var clearBtn: Button? = null
//    var parenthesesBtn: Button? = null
//    var decimalBtn: Button? = null
//    var backBtn: Button? = null
//    var themeToggle: ImageButton? = null


    private var workings: String = ""
    private var formula: String = ""
    private var tempFormula: String = ""
    private var leftBracket: Boolean = true
    var equals: Boolean = false
    private var colorButtonList: HashMap<Button?, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTextViews()
        setThemeIcon()
    }

    private fun initTextViews() {
        colorButtonList = HashMap()
        colorButtonList!![binding.zeroBtn] = 0
        colorButtonList!![binding.oneBtn] = 0
        colorButtonList!![binding.twoBtn] = 0
        colorButtonList!![binding.threeBtn] = 0
        colorButtonList!![binding.fourBtn] = 0
        colorButtonList!![binding.fiveBtn] = 0
        colorButtonList!![binding.sixBtn] = 0
        colorButtonList!![binding.sevenBtn] = 0
        colorButtonList!![binding.eightBtn] = 0
        colorButtonList!![binding.nineBtn] = 0
        colorButtonList!![binding.addBtn] = 0
        colorButtonList!![binding.subtractBtn] = 0
        colorButtonList!![binding.multiplyBtn] = 0
        colorButtonList!![binding.divideBtn] = 0
        colorButtonList!![binding.equalBtn] = 0
        colorButtonList!![binding.parenthesesBtn] = 0
        colorButtonList!![binding.clearBtn] = 0
        colorButtonList!![binding.decimalBtn] = 0
        colorButtonList!![binding.powerBtn] = 0
        colorButtonList!![binding.backBtn] = 0
    }

    private fun setWorking(value: String) {
        workings += value
        binding.workingTV.text = workings
        equate(false)
    }

    fun equalsOnclick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        equals = true
        equate(true)
        if (binding.resultTV.text.toString().isNotEmpty()) {
            binding.workingTV.text = ""
            binding.workingTV.text = binding.resultTV.text.toString()
            workings = binding.resultTV.text.toString()
        }
        val loc = IntArray(2)
        binding.workingTV.getLocationOnScreen(loc)
        val yCord = loc[1].toFloat()

        binding.resultTVtemp.text = binding.resultTV.text
        binding.resultTVtemp.clearAnimation()

        // Create Animator objects for each property
        val scaleXAnimator = ObjectAnimator.ofFloat(binding.resultTVtemp, "scaleX", 1f, 0.7f)
        val scaleYAnimator = ObjectAnimator.ofFloat(binding.resultTVtemp, "scaleY", 1f, 0.7f)
        val alphaAnimator = ObjectAnimator.ofFloat(binding.resultTVtemp, "alpha", 1f, 0f)
        val yAnimator = ObjectAnimator.ofFloat(binding.resultTVtemp, "y", yCord, yCord - 230)

        // Create a AnimatorSet to run the animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator, yAnimator)
        animatorSet.setDuration(150)

        // Add a listener to trigger the second animation after the first set completes
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val y2Animator = ObjectAnimator.ofFloat(
                    binding.resultTVtemp, "y", yCord - 230,
                    yCord + 200
                )
                val alpha2Animator = ObjectAnimator.ofFloat(
                    binding.resultTVtemp, "alpha", 0f,
                    0f
                )

                val secondAnimatorSet = AnimatorSet()
                secondAnimatorSet.playTogether(y2Animator, alpha2Animator)
                secondAnimatorSet.setDuration(1)
                secondAnimatorSet.start()
            }
        })

        // Start the first animation set
        animatorSet.start()
        binding.resultTV.text = ""
    }

    private fun equate(equalCheck: Boolean) {
        if ((workings.isNotEmpty())) {
            var result: Double? = null
            val engine = ScriptEngineManager().getEngineByName("rhino")
            checkForPowerOf()

            try {
                result = engine.eval(formula) as Double
            } catch (e: ScriptException) {
                if (equalCheck) {
                    Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show()
                    equals = false
                }
            }

            if (result != null) {
                if (result - result.toInt() != 0.0) {
                    binding.resultTV.text = result.toString()
                } else {
                    binding.resultTV.text = java.lang.String.valueOf(result.toInt())
                }
            }
        }
    }

    private fun checkForPowerOf() {
        val indexOfPowers = ArrayList<Int>()
        for (i in workings.indices) {
            if (workings[i] == '^') indexOfPowers.add(i)
        }

        formula = workings
        tempFormula = workings
        for (index in indexOfPowers) {
            changeFormula(index)
        }
        formula = tempFormula

        //when there is a number followed by a operator it messes up the calculation.
        //try writing 1+1 and the backspace the 1. answer should be 1 but is 2
        //this code solves it
        if (!(isNumeric(tempFormula[tempFormula.length - 1]))) {
            formula = tempFormula.substring(0, (tempFormula.length - 1))
        }
    }

    private fun changeFormula(index: Int) {
        var numberLeft = ""
        var numberRight = ""

        for (i in index + 1 until workings.length) {
            if (isNumeric(workings[i])) numberRight += workings[i]
            else break
        }

        for (i in index - 1 downTo 0) {
            if (isNumeric(workings[i])) numberLeft += workings[i]
            else break
        }

        val original = "$numberLeft^$numberRight"
        val changed = "Math.pow($numberLeft,$numberRight)"
        tempFormula = tempFormula.replace(original, changed)
    }

    private fun isNumeric(c: Char): Boolean {
        return (c in '0'..'9') || c == '.'
    }

    fun bracketsOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        if (leftBracket) {
            setWorking("(")
            leftBracket = false
        } else {
            setWorking(")")
            leftBracket = true
        }
    }

    fun clearOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        binding.resultTV.text = ""
        binding.workingTV.text = ""
        workings = ""
    }

    private fun randomColor(): Int {
        val minColor = Color.parseColor("#000000")
        val maxColor = Color.parseColor("#999999")

        return Color.rgb(
            (Math.random() * (Color.red(maxColor) - Color.red(minColor) + 1) + Color.red(
                minColor
            )).toInt(),
            (Math.random() * (Color.green(maxColor) - Color.green(minColor) + 1) + Color.green(
                minColor
            )).toInt(),
            (Math.random() * (Color.blue(maxColor) - Color.blue(minColor) + 1) + Color.blue(minColor)).toInt()
        )
    }

    private fun changeAllColor() {
        for (loop in colorButtonList!!.keys) {
            colorChanger(loop, colorButtonList!![loop]!!)
        }
    }

    private fun colorChanger(button: Button?, currentColor: Int) {
        val newColor = randomColor()
        val colorChange = ObjectAnimator.ofInt(
            button, "backgroundColor", currentColor,
            newColor
        )
        colorChange.setEvaluator(ArgbEvaluator())
        colorChange.setDuration(800) // Duration in milliseconds
        colorChange.start()
        colorButtonList!![button] = newColor
    }


    fun decimalOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking(".")
    }

    fun zeroOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)

        //change the above code somehow so the value for the color can be changed
        setWorking("0")
    }

    fun oneOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(6)
        setWorking("1")
    }

    fun twoOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        setWorking("2")
    }

    fun threeOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("3")
    }

    fun fourOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("4")
    }


    fun fiveOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("5")
    }

    fun sixOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("6")
    }

    fun sevenOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("7")
    }

    fun eightOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("8")
    }

    fun nineOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("9")
    }

    fun addOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("+")
    }

    fun subtractOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("-")
    }

    fun multiplyOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("*")
    }

    fun divideOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("/")
    }


    fun backOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        if (workings.isNotEmpty()) {
            val tempWorking = workings.substring(0, workings.length - 1)
            workings = tempWorking
            binding.workingTV.text = workings
        }
        equate(false)
    }


    fun powerOnClick(view: View) {
        changeAllColor()
        view.performHapticFeedback(12)
        setWorking("^")
    }

    fun toggleTheme(view: View?) {
        //Set the theme mode for the restarted activity.

        val darkMode = AppCompatDelegate.getDefaultNightMode()
        if (darkMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        // Recreate the activity for the theme change to take effect.
        recreate()
    }

    private fun setThemeIcon() {
        val darkMode = AppCompatDelegate.getDefaultNightMode()
        if (darkMode == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.themeToggle.setImageResource(R.drawable.lighttoggle)
        } else {
            binding.themeToggle.setImageResource(R.drawable.darktoggle)
        }
    }
}