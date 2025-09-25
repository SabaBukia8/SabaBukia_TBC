package com.example.lib

import kotlin.random.Random
class Calculator {
    fun calculate() {
        var ANSWER = true
        while (ANSWER == true){
            var input1: String
            do {
                print("Input 1 = ")
                input1 = readln().trim()
            } while (input1.isEmpty())

            var input2: String
            do {
                print("Input 2 = ")
                input2 = readln().trim()
            } while (input2.isEmpty())

            val num1 = getNumber(input1)
            val num2 = getNumber(input2)
            println("აირჩიეთ ერთ-ერთი ოპერაცია / * % !")
            val oprType: String = readln().trim()

            when(oprType){
                "/" -> {
                    if (num2 != 0){
                        println("$num1 და $num2 -ის განაყოფი არის: ${num1 / num2} ")
                    }
                }
                "*" -> {
                    println("$num1 და $num2 -ის ნამრავლი არის: ${num1 * num2} ")
                }
                "%" -> {
                    if (num2 != 0){
                        println("$num1-ის $num2-ზე გაყოფის შედეგად მიღებული ნაშთი არის: ${num1 % num2} ")
                    }
                }
                "!" -> {
                    if (num2 != 0){
                        println("$num1 და $num2-ის განაყოფის ფაქტორიალი არის: ${factorial(num1 / num2)} ")
                    }
                }
                else -> println("აირჩიეთ სწორი ტიპის ოპერაცია!")
            }
            var answr: String
            do {
                println("გსურს პროგრამის ხელახლა დაწყება? Y/N")
                answr = readln().trim()
            } while (answr.isEmpty() || answr != "Y" && answr != "N")
            if (answr == "N"){
                ANSWER = false
            }
        }
    }

    private fun getNumber(input: String): Int{
        val number = input.filter { it.isDigit() }
        return if (number.isNotEmpty()){
            number.toInt()
        } else {
            Random.nextInt(-127, 130)
        }
    }
    fun factorial(n: Int): Long {
        if (n < 0) {
            throw IllegalArgumentException("ფაქტორიალი ვერ იქნება უარყოფითი")
        }
        return if (n == 0 || n == 1) {
            1L
        } else {
            n * factorial(n - 1)
        }
    }
}