object Solution {
  def isValid(s: String): Boolean = {
    val stack = scala.collection.mutable.Stack[Char]()

    for (ch <- s) {
      if (ch == '(' || ch == '{' || ch == '[') stack.push(ch)

      else {
        if (stack.nonEmpty && isPair(stack.top, ch)) {
          stack.pop()
        }
        else stack.push(ch)
      }
    }
    stack.isEmpty
  }

  def isPair(c1: Char, c2: Char): Boolean =
    (c1 == '(' && c2 == ')') || (c1 == '{' && c2 == '}') ||
      (c1 == '[' && c2 == ']')
}
