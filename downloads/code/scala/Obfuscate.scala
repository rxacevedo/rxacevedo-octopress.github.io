object Obfuscate {

  case class WikiLeaks(s: String)
  case class NSA(s: String)
  case class InstantMessage(s: String)
  case class Password(s: String)

  def main(args: Array[String]) {
    println(scramble("Hello!"))
    println(jumble("Moriturus te saluto."))
    println(mungeString("I'm typing words"))
    println("Case classes\n")
    val (wklks, nsa, im, pw) = (WikiLeaks("Julian Assange"), // This is a tuple
                                NSA("Edward Snowden"),
                                InstantMessage("OMG GUESS WHO SAID WHAT"),
                                Password("puppyKisses123"))

    println("The secrets are: \n" + 
      dispatch(wklks) + 
      "\n" + 
      dispatch(nsa) + 
      "\n" + 
      dispatch(im) + 
      "\n" + dispatch(pw))

  }

  def dispatch(message: Any)= message match {
      case WikiLeaks(s: String) => scramble(message.toString)
      case NSA(s: String) => jumble(message.toString)
      case InstantMessage(s: String) => mungeString(message.toString)
      case Password(s: String) => scramble(jumble(scramble(message.toString)))
      case _ => "None"
  }

  // Old and boring
  // def obfuscate(text: String): String = {
  //   util.Random.shuffle(List.concat(text)).mkString
  // }

  def makeObfuscator(f: String => List[Char])(g: List[Char] => String) = {
    val composed = (a: String) => g(f(a))
    composed
  }

  def scramble(text: String) = {
    val f = (a: String) => util.Random.shuffle(List.concat(a))
    val g = (a: List[Char]) => a.mkString
    makeObfuscator(f)(g)(text) 
  }

  def jumble(text: String) = {
    val f = (a: String) => List.concat(a)
    val g = (a: List[Char]) => a.mkString(":>>")
    makeObfuscator(f)(g)(text)
  }

  def mungeString(text: String) = {
    val f = (a: String) => List.concat(a)
    val g = (a: List[Char]) => a.mkString("666")
    makeObfuscator(f)(g)(text)
  }


}
