package com.example.lib

class Opperation{
    fun usg(num1: Int, num2: Int): Int {
        var a = num1
        var b = num2
            while (b != 0) {
                val temp = a % b
                a = b
                b = temp
                }
            return a
        }

    fun usj(num1: Int, num2: Int): Int{
        var a = num1
        var b = num2

        return a * b / usg(a,b)
    }

    fun hasDollar(string: String): Boolean{
        for (char in string){
            if (char == '$'){
                return true
            }
        }
        return false
    }

    fun countSumHundred (num: Int = 0, finish: Int = 100): Int{
        if (num > finish)
            return 0
        return num + countSumHundred(num + 2, finish)
    }

    fun reverseNum(num: Int): Int {
        val absoluteValue = if (num < 0) -num else num
        val reversed = absoluteValue.toString().reversed().toInt()
        return if (num < 0) -reversed else reversed
    }

    fun isPalindrome(string: String): Boolean{
        if (string == string.reversed()){
            return true
        }
        return false
    }
    }
