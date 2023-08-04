# hyperskill-flashcards

- This project focused on utilizing JSON to create and manage a flashcards which was created with a mutableList with a created data class.
- Functions include:
  - "add": Creates cards with terms/definitions and adding them to list stored in memory
  - "remove": Removes flash cards from memory by inputing the term you wish to remove
  - "ask": This begins the quiz where a term/question is given and you respond with the definition/answer
  - "hardest card": After taking a quiz, this provides information related to which cards were answered incorrect the most
  - "reset stats": this resets the the quiz pass/fail stats
  - "log": exports a file with everything displayed or entered into the console
  - "exit": ends program
  - "export": after creating a list of flashcards, you can use this function to export the list to a file of your choosing so that you may import it after the program exits.
  - "import": flashcards created either in a seperate file or from cards "added" and "exported" earlier can be called with this.
  - There is also an option to pass parameters into `fun main(args: Array<String>)` that will automatically import a specified file when the program starts or automatically export to a specified file when the program "exits". If no args are present, the program runs normally. The parameters passed into `main` must follow this pattern:
  `-import filename1.txt -export filename2.txt`
