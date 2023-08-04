package flashcards

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import kotlin.system.exitProcess

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val type = Types.newParameterizedType(MutableList::class.java, CARD::class.java)!!
val jsonAdapter: JsonAdapter<MutableList<CARD>> = moshi.adapter(type)
var exportArg = ""
var importArg = ""

fun main(args: Array<String>){
    for (i in args.indices) {
        if (args[i] == "-import") importArg = args[i + 1]
        if (args[i] == "-export") exportArg = args[i + 1]
    }
    FlashCards().runApp()
}

data class CARD(var mistakes: Int = 0, var term: String, var definition: String)

class FlashCards {
    fun runApp() {
        if (importArg.isNotBlank()) mutableListOf<CARD>().import()
        else mutableListOf<CARD>().actions()
    }

    private fun MutableList<CARD>.actions() {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (readln().lowercase()) {
            "add" -> createCard()
            "remove" -> remove()
            "import" -> println("File name:").also { import() }
            "export" -> println("File name:").also { export() }
            "ask" -> ask()
            "exit" -> exit()
            "log" -> log()
            "hardest card" -> hardest()
            "reset stats" -> reset()
        }
        println().also { actions() }
    }

    private fun MutableList<CARD>.createCard() {
        println("The card:")
        val term = readln()
        this.forEach {
            if (it.term == term) println("The card \"$term\" already exists.").run { return }
        }
        println("The definition of the card:")
        val definition = readln()
        this.forEach {
            if (it.definition == definition) println("The definition \"$definition\" already exists.").run { return }
        }
        println("The pair (\"$term\":\"$definition\") has been added.")
        add(CARD(0, term, definition))
    }

    private fun MutableList<CARD>.remove() {
        println("Which card?")
        val input = readln()
        for (i in this) {
            if (i.term == input) {
                this.remove(i)
                println("The card has been removed.\n").run { actions() }
            }
        }
        println("Can't remove \"$input\": there is no such card.")
    }

    private fun MutableList<CARD>.import() {
        val storageFile =
            if (importArg.isNotBlank()) File(importArg).also { importArg = "" } else File(readln())
        if (!storageFile.exists()) println("File not found.").run { actions() }
        val importCards = jsonAdapter.fromJson(storageFile.readText())!!

        for (i in this.indices.reversed()) {
            for (importCard in importCards) {
                if (this[i].term == importCard.term) {
                    this.removeAt(i)
                    if (this.size == 0) break
                } else continue
            }
        }
        importCards.forEach { this.add(it) }
        println("${importCards.size} cards have been loaded.").run { actions() }
    }

    private fun MutableList<CARD>.export() {
        val storageFile =
            if (exportArg.isNotBlank()) File(exportArg).also { exportArg = "" } else File(readln())
        if (!storageFile.exists()) storageFile.createNewFile()
        storageFile.writeText(jsonAdapter.toJson(this))
        println("${this.size} cards have been saved.")
    }

    private fun MutableList<CARD>.ask() {
        println("How many times to ask?")
        val number = readln().toInt()
        var count = 0
        while (count != number) {
            this.forEach { card ->
                if (count == number) return
                val definition = card.definition
                println("Print the definition of \"${card.term}\":").also { count++ }
                val input = readln()
                if (input == definition) println("Correct!")
                else {
                    card.mistakes++
                    var wrong = ""
                    this.forEach { if (it.definition == input) wrong = it.term }
                    if (wrong.isNotBlank()) {
                        println("Wrong. The right answer is \"$definition\", but your definition is correct for \"${wrong}\".")
                    } else println("Wrong. The right answer is \"${definition}\".")
                }
            }
        }
    }

    private fun MutableList<CARD>.hardest() {
        val result = mutableListOf<String>()
        val newList = mutableListOf<CARD>()
        this.forEach { newList.add(it) }
        newList.sortBy { it.mistakes }

        if (newList.all { it.mistakes == 0 } || this.size == 0)
            println("There are no cards with errors.").run { actions() }
        else {
            val num = newList[newList.size - 1].mistakes
            for (card in newList) {
                if (card.mistakes == num)
                    result += card.term
            }
            if (result.size > 1) println(
                "The hardest cards are ${result.joinToString("\", \"", "\"", "\"")}. You have $num errors answering them."
            )
            else println(
                "The hardest card is \"${result.joinToString()}\". You have $num errors answering it."
            )
        }
    }

    private fun MutableList<CARD>.reset() {
        this.forEach { it.mistakes = 0 }
        println("Card statistics have been reset.")
    }

    private fun MutableList<CARD>.exit() {
        if (exportArg.isNotBlank()) export()
        println("Bye bye!").also { exitProcess(0) }
    }
}

var logCollection = listOf<String>()

fun readln(): String {
    val input = kotlin.io.readln()
    logCollection += input
    return input
}

fun println(input: String) {
    logCollection += input
    kotlin.io.println(input)
}

fun log() {
    println("File name:")
    val storageFile = File(readln())
    if (!storageFile.exists()) storageFile.createNewFile() else storageFile.delete()
        .also { storageFile.createNewFile() }
    storageFile.writeText(logCollection.joinToString("\n"))
    println("The log has been saved.\n")
}