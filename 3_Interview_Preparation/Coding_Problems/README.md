# 💻 Coding Challenge Problems

## Hands-On Practice for Technical Interviews

This directory contains a curated collection of coding problems designed specifically for Scala developers preparing for technical interviews. Each problem includes multiple solutions, time/space complexity analysis, and interview preparation tips.

---

## 🎯 Problem Categories

### **🧠 Pattern Recognition**
- **Two Pointers**: Efficiency for sorted arrays and strings
- **Sliding Window**: Dynamic programming for subsequences
- **Fast & Slow Pointers**: Cycle detection and midpoints
- **Merge Intervals**: Overlapping time ranges and sorting

### **🏗️ Data Structures Mastery**
- **Arrays & Hash Maps**: O(1) lookups and frequency counting
- **Linked Lists**: In-place operations and pointer manipulation
- **Trees & Graphs**: Traversal, path finding, and connectivity
- **Stacks & Queues**: LIFO, FIFO, and monotonic patterns

### **🧬 Advanced Algorithms**
- **Dynamic Programming**: Optimal substructure and overlapping subproblems
- **Backtracking**: Exhaustive search with pruning
- **Greedy Algorithms**: Locally optimal choices
- **Bit Manipulation**: Efficient binary operations

---

## 💼 Interview Problem Templates

### **Problem Solving Framework**
```scala
object ProblemTemplate {

  // 1. Understand the problem constraints and edge cases
  case class ProblemSpec(
    inputTypes: List[String],
    outputType: String,
    constraints: Map[String, String],
    edgeCases: List[String]
  )

  // 2. Design multiple approaches
  trait SolutionApproach {
    def description: String
    def timeComplexity: String
    def spaceComplexity: String
    def whenToUse: String
    def drawbacks: List[String]
  }

  case class TimeSpaceAnalysis(
    bestCase: String,
    average: String,
    worstCase: String,
    space: String
  )

  // 3. Implement and test thoroughly
  // 4. Consider follow-up questions and optimizations
}
```

### **Common Interview Coding Problems**

#### **Array & Matrix Problems**

**1. Maximum Subarray Sum (Kadane's Algorithm)**
```scala
object Kadane {

  // O(n) time, O(1) space
  def maxSubarraySum(nums: Array[Int]): Int = {
    if (nums.isEmpty) return 0

    var maxCurrent = nums(0)
    var maxGlobal = nums(0)

    for (i <- 1 until nums.length) {
      maxCurrent = math.max(nums(i), maxCurrent + nums(i))
      maxGlobal = math.max(maxGlobal, maxCurrent)
    }

    maxGlobal
  }

  // With subarray indices
  def maxSubarrayWithIndices(nums: Array[Int]): (Int, Int, Int) = {
    if (nums.isEmpty) return (0, -1, -1)

    var maxCurrent = nums(0)
    var maxGlobal = nums(0)
    var start = 0
    var end = 0
    var tempStart = 0

    for (i <- 1 until nums.length) {
      if (nums(i) > maxCurrent + nums(i)) {
        maxCurrent = nums(i)
        tempStart = i
      } else {
        maxCurrent += nums(i)
      }

      if (maxCurrent > maxGlobal) {
        maxGlobal = maxCurrent
        start = tempStart
        end = i
      }
    }

    (maxGlobal, start, end)
  }
}
```

**2. Two Sum - Multiple Approaches**
```scala
object TwoSumSolutions {

  // Brute Force: O(n²) time
  def twoSumBrute(nums: Array[Int], target: Int): Array[Int] = {
    for (i <- 0 until nums.length - 1) {
      for (j <- i + 1 until nums.length) {
        if (nums(i) + nums(j) == target) {
          return Array(i, j)
        }
      }
    }
    throw new IllegalArgumentException("No solution found")
  }

  // Sort + Two Pointers: O(n log n) time
  def twoSumTwoPointers(nums: Array[Int], target: Int): Array[Int] = {
    val indexed = nums.zipWithIndex.sortBy(_._1)
    var left = 0
    var right = indexed.length - 1

    while (left < right) {
      val sum = indexed(left)._1 + indexed(right)._1
      if (sum == target) {
        return Array(indexed(left)._2, indexed(right)._2).sorted
      } else if (sum < target) {
        left += 1
      } else {
        right -= 1
      }
    }
    throw new IllegalArgumentException("No solution found")
  }

  // Hash Map: O(n) time, O(n) space
  def twoSumHashMap(nums: Array[Int], target: Int): Array[Int] = {
    val map = scala.collection.mutable.HashMap[Int, Int]()
    val complementMap = scala.collection.mutable.HashMap[Int, Int]()

    for (i <- nums.indices) {
      val complement = target - nums(i)

      // Check if we saw the complement before
      complementMap.get(complement) match {
        case Some(j) => return Array(j, i)
        case None => map.put(nums(i), i)
      }

      complementMap.put(nums(i), i)
    }

    throw new IllegalArgumentException("No solution found")
  }
}
```

#### **String Manipulation Problems**

**3. Valid Parentheses**
```scala
object ValidParentheses {

  private val brackets = Map(
    ')' -> '(',
    ']' -> '[',
    '}' -> '{'
  )

  def isValid(s: String): Boolean = {
    val stack = scala.collection.mutable.Stack[Char]()

    for (char <- s) {
      char match {
        case '(' | '[' | '{' => stack.push(char)
        case ')' | ']' | '}' =>
          if (stack.isEmpty || stack.top != brackets(char)) {
            return false
          }
          stack.pop()
        case _ => // Invalid character, continue or throw
      }
    }

    stack.isEmpty
  }

  // Alternative functional approach
  def isValidFunctional(s: String): Boolean = {
    def check(remaining: String, stack: List[Char]): Boolean = {
      remaining.headOption match {
        case None => stack.isEmpty
        case Some(char) =>
          char match {
            case '(' | '[' | '{' =>
              check(remaining.tail, char :: stack)
            case ')' | ']' | '}' =>
              stack.headOption.contains(brackets.getOrElse(char, '?')) &&
              check(remaining.tail, stack.tail)
            case _ => check(remaining.tail, stack)
          }
      }
    }
    check(s, Nil)
  }
}
```

**4. Longest Substring Without Repeating Characters**
```scala
object LongestSubstring {

  // Sliding Window with Set: O(n) time
  def lengthOfLongestSubstring(s: String): Int = {
    val chars = scala.collection.mutable.Set[Char]()
    var maxLength = 0
    var left = 0

    for (right <- s.indices) {
      // Remove characters from left until no duplicate
      while (chars.contains(s(right))) {
        chars.remove(s(left))
        left += 1
      }

      chars.add(s(right))
      maxLength = math.max(maxLength, right - left + 1)
    }

    maxLength
  }

  // Alternative with ASCII array frequency
  def lengthOfLongestSubstringASCII(s: String): Int = {
    val lastSeen = Array.fill(256)(-1)
    var maxLength = 0
    var left = 0

    for (right <- s.indices) {
      val char = s(right)
      if (lastSeen(char) >= left) {
        left = lastSeen(char) + 1
      }
      lastSeen(char) = right
      maxLength = math.max(maxLength, right - left + 1)
    }

    maxLength
  }
}
```

#### **Tree & Graph Problems**

**5. Binary Tree Inorder Traversal**
```scala
object BinaryTreeTraversal {

  case class TreeNode(value: Int, left: Option[TreeNode] = None, right: Option[TreeNode] = None)

  // Recursive inorder
  def inorderTraversal(root: Option[TreeNode]): List[Int] = root match {
    case None => Nil
    case Some(node) =>
      inorderTraversal(node.left) ::: List(node.value) ::: inorderTraversal(node.right)
  }

  // Iterative inorder with stack
  def inorderTraversalIterative(root: Option[TreeNode]): List[Int] = {
    val result = collection.mutable.ListBuffer[Int]()
    val stack = collection.mutable.Stack[TreeNode]()
    var current = root

    while (current.isDefined || stack.nonEmpty) {
      // Go to leftmost node
      while (current.isDefined) {
        stack.push(current.get)
        current = current.get.left
      }

      // Visit node
      current = Some(stack.pop())
      result += current.get.value
      current = current.get.right
    }

    result.toList
  }

  // Morris Traversal - O(1) space
  def morrisTraversal(root: Option[TreeNode]): List[Int] = {
    val result = collection.mutable.ListBuffer[Int]()
    var current = root
    var predecessor: Option[TreeNode] = None

    while (current.isDefined) {
      if (current.get.left.isEmpty) {
        // No left subtree, visit current
        result += current.get.value
        current = current.get.right
      } else {
        // Find inorder predecessor
        predecessor = Some(current.get.left.get)
        while (predecessor.get.right.isDefined && predecessor.get.right.get != current.get) {
          predecessor = predecessor.get.right
        }

        if (predecessor.get.right.isEmpty) {
          // Link to current and move to left
          predecessor.get.right = current
          current = current.get.left
        } else {
          // Unlink and visit current
          predecessor.get.right = None
          result += current.get.value
          current = current.get.right
        }
      }
    }

    result.toList
  }
}
```

**6. Graph Valid Tree** (Union-Find Implementation)
```scala
object GraphValidTree {

  // Union-Find data structure
  class UnionFind(size: Int) {
    private val parent = Array.tabulate(size)(identity)
    private val rank = Array.fill(size)(0)

    def find(x: Int): Int = {
      if (parent(x) != x) {
        parent(x) = find(parent(x)) // Path compression
      }
      parent(x)
    }

    def union(x: Int, y: Int): Boolean = {
      val rootX = find(x)
      val rootY = find(y)

      if (rootX != rootY) {
        // Union by rank
        if (rank(rootX) > rank(rootY)) {
          parent(rootY) = rootX
        } else if (rank(rootX) < rank(rootY)) {
          parent(rootX) = rootY
        } else {
          parent(rootY) = rootX
          rank(rootX) += 1
        }
        true
      } else {
        false // Cycle detected
      }
    }
  }

  def validTree(n: Int, edges: Array[Array[Int]]): Boolean = {
    if (edges.length != n - 1) return false // Tree must have n-1 edges

    val uf = new UnionFind(n)
    var hasCycle = false

    for (edge <- edges) {
      val (u, v) = (edge(0), edge(1))
      if (!uf.union(u, v)) {
        hasCycle = true
        // Continue to check all edges even if cycle found
      }
    }

    !hasCycle && uf.find(0) == uf.find(n-1) // Connected component
  }

  // Alternative DFS approach (creates graph structure)
  def validTreeDFS(n: Int, edges: Array[Array[Int]]): Boolean = {
    if (edges.length != n - 1) return false

    // Build adjacency list
    val graph = Array.fill(n)(List[Int]())
    for (edge <- edges) {
      val (u, v) = (edge(0), edge(1))
      graph(u) = v :: graph(u)
      graph(v) = u :: graph(v)
    }

    val visited = Array.fill(n)(false)
    var hasCycle = false

    def dfs(node: Int, parent: Int): Unit = {
      visited(node) = true

      for (neighbor <- graph(node)) {
        if (!visited(neighbor)) {
          dfs(neighbor, node)
        } else if (neighbor != parent) {
          hasCycle = true
        }
      }
    }

    dfs(0, -1)
    !hasCycle && visited.forall(identity) // Connected and no cycles
  }
}
```

---

## 🔍 Problem-Solving Patterns

### **Sliding Window Maximum**
```scala
object SlidingWindow {

  // O(n) solution using deque
  def maxSlidingWindow(nums: Array[Int], k: Int): Array[Int] = {
    val deque = collection.mutable.ArrayDeque[Int]()
    val result = collection.mutable.ArrayBuffer[Int]()

    for (i <- nums.indices) {
      // Remove elements outside window
      while (deque.nonEmpty && deque.head < i - k + 1) {
        deque.removeHead()
      }

      // Remove smaller elements from back (maintain decreasing order)
      while (deque.nonEmpty && nums(deque.last) <= nums(i)) {
        deque.removeLast()
      }

      // Add current element index
      deque.append(i)

      // Add maximum to result when window is formed
      if (i >= k - 1) {
        result.append(nums(deque.head))
      }
    }

    result.toArray
  }

  // Alternative O(n*k) approach for comparison
  def maxSlidingWindowNaive(nums: Array[Int], k: Int): Array[Int] = {
    (for (i <- 0 to nums.length - k) yield {
      nums.slice(i, i + k).max
    }).toArray
  }
}
```

### **Word Ladder (BFS)**
```scala
object WordLadder {

  def ladderLength(beginWord: String, endWord: String, wordList: List[String]): Int = {
    val wordSet = wordList.toSet
    val queue = collection.mutable.Queue[(String, Int)]()
    val visited = collection.mutable.Set[String]()

    if (!wordSet.contains(endWord)) return 0

    queue.enqueue((beginWord, 1))
    visited.add(beginWord)

    while (queue.nonEmpty) {
      val (currentWord, level) = queue.dequeue()

      if (currentWord == endWord) return level

      // Generate all possible transformations
      val transformations = for {
        i <- currentWord.indices
        char <- 'a' to 'z'
        if char != currentWord(i)
        newWord = currentWord.updated(i, char)
        if wordSet.contains(newWord) && !visited.contains(newWord)
      } yield newWord

      for (word <- transformations) {
        visited.add(word)
        queue.enqueue((word, level + 1))
      }
    }

    0 // No transformation found
  }

  // Bidirectional BFS for optimization
  def ladderLengthBidirectional(beginWord: String, endWord: String, wordList: List[String]): Int = {
    val wordSet = wordList.toSet
    if (!wordSet.contains(endWord)) return 0

    val beginQueue = collection.mutable.Queue[String]()
    val endQueue = collection.mutable.Queue[String]()
    val beginVisited = collection.mutable.Set[String]()
    val endVisited = collection.mutable.Set[String]()

    beginQueue.enqueue(beginWord)
    endQueue.enqueue(endWord)
    beginVisited.add(beginWord)
    endVisited.add(endWord)

    var steps = 1

    while (beginQueue.nonEmpty && endQueue.nonEmpty) {
      // Expand from begin side
      val beginSize = beginQueue.size
      for (_ <- 0 until beginSize) {
        val word = beginQueue.dequeue()
        if (endVisited.contains(word)) return steps

        val neighbors = getNeighbors(word, wordSet)
        for (neighbor <- neighbors) {
          if (!beginVisited.contains(neighbor)) {
            beginVisited.add(neighbor)
            beginQueue.enqueue(neighbor)
          }
        }
      }

      steps += 1

      // Expand from end side
      val endSize = endQueue.size
      for (_ <- 0 until endSize) {
        val word = endQueue.dequeue()
        if (beginVisited.contains(word)) return steps - 1

        val neighbors = getNeighbors(word, wordSet)
        for (neighbor <- neighbors) {
          if (!endVisited.contains(neighbor)) {
            endVisited.add(neighbor)
            endQueue.enqueue(neighbor)
          }
        }
      }
    }

    0
  }

  private def getNeighbors(word: String, wordSet: Set[String]): List[String] = {
    (for {
      i <- word.indices
      char <- 'a' to 'z'
      if char != word(i)
      newWord = word.updated(i, char)
      if wordSet.contains(newWord)
    } yield newWord).toList
  }
}
```

---

## 🎯 Interview Preparation Strategy

### **Time Management**
- **Easy Problems**: 15-20 minutes (60% accuracy needed)
- **Medium Problems**: 30-45 minutes (40% accuracy needed)
- **Hard Problems**: 60+ minutes (20% accuracy needed)

### **Problem Categories Distribution**
```
Arrays/Hashing:      40% (Most common)
Strings:             25% (Very common)
Trees/BST:           15% (Common)
Graphs:               10% (Less common)
Dynamic Programming:  10% (Tricky)
```

### **Practice Quality Over Quantity**
- Focus on understanding patterns, not memorizing solutions
- Practice explaining solutions out loud
- Study multiple approaches for the same problem
- Identify your weak areas and target them specifically

### **Follow-Up Questions Preparation**
- **Time/Space Complexity**: Always discuss Big O
- **Edge Cases**: Consider input validation and boundary conditions
- **Alternative Approaches**: Show breadth of knowledge
- **Optimization**: Discuss potential improvements

---

## 🏆 Problem Difficulty Rating

### **Beginner Level**
- Basic array operations (Two Sum, Maximum Subarray)
- String fundamentals (Valid Parentheses, Palindrome)
- Simple data structures (Stack for parentheses)

### **Intermediate Level**
- Two pointers and sliding windows
- Tree traversals and basic BST operations
- Graph BFS/DFS with adjacency lists
- Basic dynamic programming (Fibonacci, Knapsack)

### **Advanced Level**
- Advanced graphs (Dijkstra, topological sort)
- Complex DP (Longest Common Subsequence, Edit Distance)
- Bit manipulation and advanced math
- System-level considerations and optimizations

---

*"Coding interviews are not about knowing everything. They're about demonstrating clear thinking, strong fundamentals, and the ability to learn and adapt. Focus on patterns, not memorization."*

**Master these problems through deliberate practice, and you'll be confidently solving any coding interview challenge that comes your way!** 🚀
