---
layout: post
title: "First-class Functions"
date: 2014-01-07 19:25:44 -0500
comments: true
categories: clojure scala programming
---

Functions that take functions as arguments and functions that return functions
as their results can be a somewhat tricky concept to grasp coming from a
purely object-oriented style of programming. I will say up front that higher-order 
functions are mostly useful for list-processing, but we will soon see
that this enables us to affect changes on data (or data structures) without
actually modifying state, which comes in quite handy when writing threaded code.

``` clojure Le map
map 
;; #<core$map clojure.core$map@17448423> 
```

Map? That's it? Yes, map is a function that takes a function and a number of
collections and will apply that function in one of two ways

1. To each element of the collection - f(element) 
2. To the group of elements that are at equal positions in the collections -
   f(element1, element2, element3)

One can easily observe the usefulness of higher-order functions (or HOFs) in this sense.
Here is a contrived example of mapping an anonymous function:

``` clojure Our first HOF
(map #(assoc % :cry (condp = (:type %)
                      :cat "Meow"
                      :dog "Woof"
                      "HELP"))
     [{:name "Spot" :type :dog} 
      {:name "Whiskers" :type :cat}                                     
      {:name "Leila" :type :dog} 
      {:name "Geronimo" :type :cat}
      {:name "Izolda the Russian mail-order bride"}])
;; ({:cry "Woof", :name "Spot", :type :dog} 
;;  {:cry "Meow", :name "Whiskers", :type :cat} 
;;  {:cry "Woof", :name "Leila", :type :dog} 
;;  {:cry "Meow", :name "Geronimo", :type :cat}
;;  {:cry "HELP", :name "Izolda the Russian mail-order bride"})
```

*Ok, we get it Roberto, there are some neat functions available in Scala and
Clojure that let us pass functions in, but why would we ever want to write our
own?*

We need to think about our functions not just in terms of what they do, but how
they do it, the order that it's done in, and the types returned by each function
application. Let's write a function to obfuscate text as an example:

``` clojure Not so HOF
(defn obfuscate [text]
  (clojure.string/join (interleave (rest (iterate rand-int 42))
    (reverse (seq text)))))
```

Or, using a threading macro for clarity (hey, some people don't like
the parentheses):

``` clojure Still not so HOF
(defn obfuscate [text]
  (->> text
        seq
        reverse
        (interleave (rest (iterate rand-int 42)))
        clojure.string/join))
```

There are some components to this function that we would like to keep, i.e. the
call to seq to convert the string into a list of chars. But what about about the
call to reverse? We could easily omit this or replace it with another function.
We might also want to join the string back together using a character, and we
might not want to interleave with numbers, but rather with symbols. How could
we do this? We can remove these components and replace them with variables, and
then pass functions in that will be bound to those variables.

``` clojure A higher-order function that takes a String and returns a function
(defn make-obfuscator [text]
  (ƒ [f g h]
      (f (g (h (seq text))))))
```

There are a variety of ways that one can achieve this in Scala, I am using function
currying here (each function takes a single parameter).

``` scala A higher-order function that takes a String and returns a function
def makeObfuscator(f: String => List[Char])(g: List[Char] => String)(s:String) = { 
  val lambda = (a: String) => g(f(a))
  lambda(s)
}
```

We can call this function and directly pass in the functions that we want like
this:

``` clojure Passing functions as arguments
((make-obfuscator clojure.string/join #(interleave (rest (iterate rand-int 42))
%) reverse) "Hello")
;; "12o7l2l1e0H"
```

We can also harness the power of this higher-order function now, to define
multiple obfuscation functions (note: these are just examples, they are not meant to provide any sort of security):

``` clojure MOAR OBFUSCATORS!
(defn scramble [text]
  ((make-obfuscator clojure.string/join #(interleave (rest (iterate rand-int 42)) %) reverse) text))

(defn jumble [text]
  ((make-obfuscator #(clojure.string/join "_" %) #(shuffle (seq %)) clojure.string/upper-case) text))

(defn move-yo-chars-every-every-char [text]
  ((make-obfuscator #(apply str (shuffle %)) #(interleave (map char (iterate inc 33)) %) reverse) text))
```

Let's test that they work:

``` clojure I'm writing stuff
(scramble "Moriturus te saluto.")
;; "27.11o1t0u0l0a0s0 0e0t0 0s0u0r0u0t0i0r0o0M"

(jumble "Moriturus te saluto.")
;; "S_ _ _\\_\\_P_P_ _\\_C_ _ _E_ _E_ _ _\\_ _U_ _S_A_)_ _._\\_\\_R_\\_U_\\_L__S_\\_\\_O_A_\\_ _ _\\_ _E_R_ _O_C_\\_T_\\_T_T_\\_\\_U_ _\\_I_(_S_\\_\\_M_A_\\__ "

(move-yo-chars-every-every-char "Moriturus te saluto.")
;; "a1t # $o2ie30%.u)sr!*-,tu&.Mu/t4o'\"r(sl+"
```
As you can see, higher-order functions can be used to generate many new
functions that share a common behavior pattern.

Here is less complex version in Scala that uses case classes to switch on message
types: 

{% include_code scala/Obfuscate.scala %}
