---
layout: post
title: "Scala and Clojure List Operations"
date: 2013-12-18 19:32
comments: true
categories: 
---

Functional programming is an incredibly powerful paradigm that empowers developers to be more flexible in the ways that they devise and implement solutions to problems. One of the most powerful features of functional programming, regardless of language, is the manner in which it allows us to interact with data structures. Provided here are some examples of "list functions" in both Scala and Clojure:

## Creating a list:

#### Scala
``` scala
List(1, 2, 3, 4, 5)
// res10: List[Int] = List(1, 2, 3, 4, 5)
```

List() is a constructor, it takes varargs and returns an immutable list (scala.collection.immutable.List). If you want a mutable list, you should use an ArrayBuffer(scala.collection.mutable.ArrayBuffer).

#### Clojure
``` clojure
'(1 2 3 4 5)
;; (1 2 3 4 5)
```

Clojure embraces the use of literals for lists and maps. 

## Filling a list:

Sometimes you'll want to fill a list with repeated values, i.e. if you are using a list to record, say, the initial state of a game. The game state would be based on how many pieces are on the board in this case, which we represent as strings inside of a list. 

#### Scala
``` 
val gamePieces: List[String] = List.fill(10)("piece")
// gamePieces: List[String] = List(piece, piece, piece, piece, piece, piece, piece, piece, piece, piece)
```

#### Clojure
``` clojure 
(repeat 10 "piece")
;; ("piece" "piece" "piece" "piece" "piece" "piece" "piece" "piece" "piece" "piece")
```
## Acquiring a range:

In Java, one might write something like this to get a list of integers 0 - 10 exclusive:

#### Java
``` java
int[] myInts = new int[10];

for (int i = 0; i < myInts.length; i++) {
  myInts[i] = i;
}
```

In Scala or Clojure, we can simplify our approach as follows:

#### Scala
``` scala
List.range(0, 10)
// res13: List[Int] = List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
```

#### Clojure
``` clojure
(range 0 10)
;; (0 1 2 3 4 5 6 7 8 9)
```

## Applying functions to lists with map

Most of the time, we will want to do more than just create lists,  we want to interact with the data inside of our lists, applying changes to them, etc. To do this, we can use the map function. Map takes a list and applies some function to each element in the list. Provided here are some map usage examples:

#### Scala
``` scala
gamePieces.map(a => a + 1)
// res17: List[String] = List(piece1, piece1, piece1, piece1, piece1, piece1, piece1, piece1, piece1, piece1)
```

*Notice how in Scala, the value of 1 is coerced to a String. This is a noteworthy difference between the two. Clojure will happily compile the equivalent code for us, but will throw a ClassCastException (RuntimeException) when we attempt to invoke the method or evaluate the var:*

#### Clojure
``` clojure
(let [pieces (repeat 10 "piece")]
  (map #(+ % 1) pieces))
;; ClassCastException java.lang.String cannot be cast to java.lang.Number  clojure.lang.Numbers.add (Numbers.java:126)
```

We can get around this using the str function, which will coerce its args to Strings and combine them into a single string:

#### Clojure
``` clojure
(let [pieces (repeat 10 "piece")]
  (map #(str % 1) pieces))
;; ("piece1" "piece1" "piece1" "piece1" "piece1" "piece1" "piece1" "piece1" "piece1" "piece1")
```

In both examples, you can see that I have passed some instruction to the map function, similar to a method. These are referred to as anonymous functions, or lambdas. Here we make all of our list members scream loudly: 

#### Scala
``` scala
gamePieces.map(a => a.toUpperCase)
// res21: List[String] = List(PIECE, PIECE, PIECE, PIECE, PIECE, PIECE, PIECE, PIECE, PIECE, PIECE)
```

#### Clojure
``` clojure
(let [pieces (repeat 10 "piece")]
  (map clojure.string/upper-case pieces))
;; ("PIECE" "PIECE" "PIECE" "PIECE" "PIECE" "PIECE" "PIECE" "PIECE" "PIECE" "PIECE")
```

## Getting relevant data with filter


There will come a time when you will have a list and you will want to extract certain values from it. For this, we can use the filter functions that Scala and Clojure provide to slice and dice our data. Lets have some fun with an example involving Hobbits (inspired by [nonrecursive](https://twitter.com/nonrecursive) at [braveclojure](http://www.braveclojure.com/6-do-things/#3_10__Hobbit_Violence)):

#### Scala
``` scala
val names= List("Bilbo Baggins", 
                "Frodo Baggins", 
                "Samwise Gamgee", 
                "Peregrin 'Pippin' Took", 
                "Meradoc 'Merry' Brandybuck")
// hobbits: List[String] = List(Bilbo Baggins, Frodo Baggins, Samwise Gamgee, Peregrin 'Pippin' Took, Meradoc 'Merry' Brandybuck)
```

#### Clojure
``` clojure
["Bilbo Baggins”
 "Frodo Baggins"
 "Samwise Gamgee"
 "Peregrin 'Pippin' Took"
 "Meriadoc 'Merry' Brandybuck"]
;; ["Bilbo Baggins" "Frodo Baggins" "Samwise Gamgee" "Peregrin 'Pippin' Took" "Meriadoc 'Merry' Brandybuck"] 
```

Now we have a list of famous hobbits, great! Chance has it that I'm throwing a special hobbit party, but only the Bagginses are invited. We need to get rid of everyone that's not a Baggins. Let's filter out the Bagginses and get with the merriment already!

#### Scala
``` scala
names.filter(a => a.endsWith("Baggins"))
// res77: List[String] = List(Bilbo Baggins, Frodo Baggins)
// Party time
```

#### Clojure
``` clojure
(let [names ["Bilbo Baggins"
             "Frodo Baggins"
             "Samwise Gamgee"
             "Peregrin 'Pippin' Took"
             "Meriadoc 'Merry' Brandybuck"]
      bagginses (filter #(re-find #".*Baggins" %) names)]
  (map #(str % " is soOOOOOO DRUNK!!") bagginses))
;; ("Bilbo Baggins is soOOOOOO DRUNK!!" "Frodo Baggins is soOOOOOO DRUNK!!")
```

Awesome! Now let's turn to a slightly more complex example - let's take on the role of Gollum and hunt for The Ring. The goal is to kill the hobbit with the precious and take it back for us. Let's find and kill the dirty lying hobbitses that has our precious!

#### Scala
``` scala
val hobbits: List[Map[String, Any]] = names.map(a => Map("name" -> a, "height" -> "small", "hairyFeet" -> true, "hasPrecious?" -> (if (a startsWith "Frodo") true else false)))
// res83: List[scala.collection.immutable.Map[String,Any]] = 
// List(Map(name -> Bilbo Baggins, height -> small, hairyFeet? -> true, hasPrecious? -> false), 
//      Map(name -> Frodo Baggins, height -> small, hairyFeet? -> true, hasPrecious? -> true), 
//      Map(name -> Samwise Gamgee, height -> small, hairyFeet? -> true, hasPrus? -> false), 
//      Map(name -> Peregrin 'Pippin' Took, height -> small, hairyFeet? -> true, hasPrecious? -> false), 
//      Map(name -> Meradoc 'Merry' Brandybuck, height -> small, hairyFeet? -> true, hasPrecious? -> false))

hobbits.filter((a: Map[String, Any]) => a.get("hasPrecious?") == Some(false))
// res84: List[Map[String,Any]] = 
// List(Map(name -> Bilbo Baggins, height -> small, hairyFeet -> true, hasPrecious? -> false), 
//      Map(name -> Samwise Gamgee, height -> small, hairyFeet -> true, hasPrecious? -> false), 
//      Map(name -> Peregrin 'Pippin' Took, height -> small, hairyFeet -> true, hasPrecious? -> false), 
//      Map(name -> Meradoc 'Merry' Brandybuck, height -> small, hairyFeet -> true, hasPrecious? -> false))
```

Having to deal with Some() is kind of ugly, so instead of a mixed-value map, we’ll switch to a case class. Here is the full implementation:

``` scala

object KillHobbits {

case class Hobbit(name: String, height: String, hairyFeet: Boolean, hasPrecious: Boolean)

  def main(args: Array[String]) {

    val names: List[String] = List("Bilbo Baggins", 
                                   "Frodo Baggins", 
                                   "Samwise Gamgee", 
                                   "Peregrin 'Pippin' Took", 
                                   "Meradoc 'Merry' Brandybuck")

    val hobbits: List[Hobbit] = makeHobbits(names)
    println("Before killing off the one holding the ring...")
    hobbits.foreach(a => println(a))
    val killTheRingKeeper: List[Hobbit] = hobbits.filter(a => a.hasPrecious == false)
    println("\nDie dirty hobbitses!")
    killTheRingKeeper.foreach(a => println(a))

  }

  def makeHobbits(names: List[String]): List[Hobbit] = {
    names.map(a => 
      Hobbit(name = a, 
             height = "hobbit-sized", 
             hairyFeet = true, 
             hasPrecious = if (a startsWith "Frodo") true else false))
  }

}

``` 

#### Clojure
``` clojure
(let [names ["Bilbo Baggins"
             "Frodo Baggins"
             "Samwise Gamgee"
             "Peregrin 'Pippin' Took"
             "Meriadoc 'Merry' Brandybuck"]
      hobbits (vec (map #(hash-map :name %     
                                   :height "hobbit-sized"
                                   :hairy-feet? true
                                   :has-precious? (if (= % "Frodo Baggins") true
                                                      false)) names))]
  (filter :has-precious? hobbits))
;; ({:name "Frodo Baggins", :hairy-feet? true, :has-precious? true, :height "hobbit-sized"})
```
