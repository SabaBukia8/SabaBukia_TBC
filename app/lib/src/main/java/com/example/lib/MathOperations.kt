package com.example.lib

class MathOperations {
    fun USG(num1: Int, num2: Int): Int {
        var a = num1
        var b = num2
            while (b != 0) {
                val temp = a % b
                a = b
                b = temp
                }
            return a
        }

    fun USJ(num1: Int, num2: Int): Int{
        var a = num1
        var b = num2

        return a * b / USG(a,b)
    }
    }
