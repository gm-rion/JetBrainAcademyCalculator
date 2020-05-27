package calculator

import java.math.BigInteger
import java.util.*

private var stack = mutableListOf<String>()
private var queue = mutableListOf<String>()

fun main() {
    val scanner = Scanner(System.`in`)
    var isWork = true
    val varibles = mutableMapOf<String, BigInteger>()

    while (isWork) {
        var nextLine = scanner.nextLine()

        // check and add varibles
        if (nextLine.contains("=")) {
            if (checkVaribles(nextLine, varibles)) {
                continue
            }
        }

        // check parentheses
        if (!checkParentheses(nextLine)) {
            continue
        }

        if (isNumber(nextLine.trim())) {
            println(nextLine)
            continue
        }

        //check isNumber
        val listStr = parseStrungToList(nextLine)

        //check
        if (nextLine.trim() == "") {
            continue
        }

        if (nextLine.trim() == "/exit") {
            println("Bye!")
            isWork = false
        } else if (nextLine.trim() == "/help") {
            println("The program calculates the sum, subtraction, multiplication and division of numbers!")
        } else if (nextLine.trim().first() == '/') {
            println("Unknown command")
        } else {

            //check and add operators
            if (!checkOperator(listStr)) {
                continue
            }

            // change varibles to value
            if (!checkVariblesInExpretion(listStr,varibles)) {
                continue
            }

            //calculate result
            getPostFixEx(listStr)
            calcPostFix()
        }
    }
}

fun parseStrungToList(nextLine: String): MutableList<String> {
    val listStr = mutableListOf<String>()
    val replace = nextLine.replace(" ", "")
    val listVal = replace.split("\\p{Punct}+".toRegex()).toMutableList()

    for (i in 0..listVal.lastIndex) {
        listVal[i] = listVal[i].trim()
    }

    while (listVal.contains("")) {
        listVal.remove("")
    }
    val listOpr = replace.split("[A-Za-z0-9]+".toRegex()).toMutableList()

    for (i in 0..listOpr.lastIndex) {
        listOpr[i] = listOpr[i].trim()
    }

    while (listOpr.contains("")) {
        listOpr.remove("")
    }

    for (i in 0..listVal.lastIndex) {
        listStr.add(listVal[i])
        if (i < listOpr.size) {
            listStr.add(listOpr[i])
        }
    }

    val strTemp = mutableListOf<String>()
    for (i in 0..listStr.lastIndex) {
        if (listStr[i].contains('(') || listStr[i].contains(')')) {
            var tempVar = ""
            for (ch in listStr[i]) {
                if (ch == '(' || ch == ')') {
                    if (tempVar != "") {
                        strTemp.add(tempVar)
                        tempVar = ""
                    }
                    strTemp.add(ch.toString())
                } else {
                    tempVar += ch.toString()
                }
            }
            if (tempVar != "") {
                strTemp.add(tempVar)
            }
        } else {
            strTemp.add(listStr[i])
        }
    }

    return strTemp
}

fun checkVariblesInExpretion(listStr: MutableList<String>, varibles: MutableMap<String, BigInteger>): Boolean {
    for (i in 0..listStr.lastIndex) {
        if (listStr[i][0].isLetter()) {
            if (varibles.containsKey(listStr[i])) {
                listStr[i] = (varibles.get(listStr[i]) ?: 0).toString()
            } else {
                println("Unknown variable")
                return false
            }
        }
    }
    return true
}

fun checkParentheses(nextLine: String): Boolean {
    if (nextLine.contains('(') || nextLine.contains(')')) {
        var leftParentheses = 0
        var rightParentheses = 0

        for (ch in nextLine){
            if (ch == '(') {
                leftParentheses++
            } else if (ch == ')') {
                rightParentheses++
            }
        }
        if (leftParentheses == rightParentheses) {
            return true
        } else {
            println("Invalid expression")
            return false
        }
    } else {
        return true
    }
}

fun checkVaribles(nextLine: String, varibles: MutableMap<String, BigInteger>): Boolean {
    var st = nextLine.replace(" ", "")
    val listStr = st.split("=")
    val chArr = listStr[0].toCharArray()

    // varibles name contain digit
    for (a in chArr) {
        if (a.isDigit()) {
            println("Invalid identifier")
            return true
        }
    }

    //list contain more then 2 element or value not number
    if (listStr.size > 2) {
        println("Invalid assignment")
        return true
    } else if (!isNumber(listStr[1])) {
        if (varibles.containsKey(listStr[1])) {
            varibles.put(listStr[0], varibles[listStr[1]]!!)
            return true
        } else {
            println("Invalid assignment")
            return true
        }
    } else {
        varibles.put(listStr[0], listStr[1].toBigInteger())
        return true
    }
}


fun isNumber(str: String): Boolean {
    return when (str.toBigIntegerOrNull()) {
        null -> false
        else -> true
    }
}

fun checkOperator(listStr: MutableList<String>): Boolean {

    for (i in 0..listStr.lastIndex) {

        if (listStr[i].contains('-') || listStr[i].contains('+')) {
            val chrArr = listStr[i].toCharArray()
            var operator = '+'

            for (ch in chrArr) {
                if (operator == '+') {
                    if (ch == '-') {
                        operator = '-'
                    } else if (ch == '+') {
                        operator == '+'
                    }

                } else if (operator == '-') {
                    if (ch == '-') {
                        operator = '+'
                    } else if (ch == '+') {
                        operator == '-'
                    }
                }
            }
            listStr[i] = operator.toString()

        } else if (listStr[i].contains('*') && listStr[i].length > 1 ) {
            println("Invalid expression")
            return false
        }
    }
    return true
}

private fun getPostFixEx(expressionList: MutableList<String>) {
    stack = mutableListOf<String>()
    queue = mutableListOf<String>()

    expressionList.forEach {
        when {

            it == "(" -> push(it)

            it == ")" -> {
                if (expressionList.contains("(")) {
                    pop()
                }
            }

            Regex("[\\d]").containsMatchIn(it) -> addQueue(it)

            Regex("[+-]").containsMatchIn(it) ->

                if (stack.isEmpty() || stack.last() == "(") push(it)

                else if (stack.last().contains(Regex("[/*]"))) {
                    pop()
                    push(it)
                }

                else {
                    addQueue(stack.last())
                    stack[stack.lastIndex] = it
                }

            Regex("[*/]").containsMatchIn(it) -> {

                if (stack.isNotEmpty() && (stack.last() == "*" || stack.last() == "/")) {
                    pop()
                }

                push(it)
            }
        }
    }

    if (stack.isNotEmpty()) {
        for (i in stack.lastIndex downTo 0) {
            if (stack[i] != "(") {
                addQueue(stack[i])
            }
        }
    }
}

private fun pop() {
    // unload stack in queue when don't search left breacket, and remove breacket
    Loop@ for (i in stack.lastIndex downTo 0) {
        if (stack[i] == "(") {
            stack[i] = " "
            break@Loop
        }
        addQueue(stack[i])
        stack[i] = " "
    }
    stack.removeIf { it == " " }
}

private fun addQueue(item: String) {
    queue.add(item)
}

private fun push(item: String) {
    stack.add(item)
}

private fun calcPostFix() {
    //Create stack
    val stack = mutableListOf<BigInteger>()

    for (item in queue) {
        when {
            // If item is number, add to stack
            Regex("[\\d]").containsMatchIn(item) -> {
                stack.add(item.toBigInteger())
            }

            //If item is + , get two last element and execute that operation
            item == "+" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] + stack.last()
                stack.removeAt(stack.lastIndex)
            }

            // If item is * , get two last element and execute that operation
            item == "*" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] * stack.last()
                stack.removeAt(stack.lastIndex)
            }

            // If item is / , get two last element and execute that operation
            item == "/" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] / stack.last()
                stack.removeAt(stack.lastIndex)
            }

            // If item is - , get two last element and execute that operation
            item == "-" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] - stack.last()
                stack.removeAt(stack.lastIndex)
            }
        }
    }

    // Результат - это элемент, который остался в стеке
    println(stack.first())
}