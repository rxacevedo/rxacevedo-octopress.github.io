---
layout: post
title: "Approximating The Golden Ratio"
date: 2014-02-09 12:47:20 -0500
comments: true
categories: clojure, math
---

This will be short post, inspired by a problem I encountered while
working through chapter 3 of
[Concrete Abstractions](https://gustavus.edu/+max/concrete-abstractions.html)
a few months back. We are tasked with writing a procedure that
approximates the Golden Ratio within a certain tolerance. Recall that
the Golden Ratio is defined as:

``` text
a/b = (a + b)/a
```

With a little math, we can restate this as a recurrence relation,
which provides the key for writing our procedure:

``` text
Φ = a/b

a/b = (a + b)/a
    = a/a + b/a
    = 1 + b/a
    = 1 + 1/(a/b)

a/b = 1 + 1/(a/b)

Φ = 1 + 1/Φ
```

Now that we can state the Golden Ratio as a function of itself, we can
write a procedure that will approxiate it within a certain tolerance.
To know when to stop, we are given a valuable piece of information:
the error of each approximation is less than 1 over the square of its
denominator. This means that we can stop `recur`ring when 1/(b*b) is
less than our acceptable tolerance. Our final function can be set up
as:

``` clojure
(defn approximate-golden-ratio [tolerance]
  (let [improve (fn [guess]
                  (+ 1 (/ 1 guess)))]
    (loop [start 1]
      (let [d (if (= clojure.lang.Ratio (type start))
                (denominator start)
                1)]
        (if (> tolerance (/ 1 (* d d)))
          start
          (recur (improve start)))))))
```

The above check on the type of our start argument is necessary because
in Clojure, 1/1 evaluates to 1 immediately, so calling `denominator`
on it will throw a `ClassCastException` right off the bat. I could not
find a way around this issue, not even with quoting:

``` clojure
'1/1
;;-> 1

'(1/1)
;;-> (1)
```

Now with our function in place, we can approximate the Golden Ratio:

``` clojure
(approximate-golden-ratio 1/50000)
;;-> 377/233

(double (approximate-golden-ratio 1/50000))
;;-> 1.618025751072961
```

While we're here, I'd like to demonstrate something about the Golden
Ratio that is pretty neat: each approximation (using this method of
rational approximations) is a **ratio of successive Fibonacci
numbers**. Let's modify our function to print these ratios as it
loops:

``` clojure
(defn approximate-golden-ratio [tolerance]
  (let [improve (fn [guess]
                  (+ 1 (/ 1 guess)))]
    (loop [start 1]
      (println (str "Current guess: " start))
      (let [d (if (= clojure.lang.Ratio (type start))
                (denominator start)
                1)]
        (if (> tolerance (/ 1 (* d d)))
          start
          (recur (improve start)))))))
```

Now when we call it, we will see successive ratios of the Fibonacci numbers:

``` clojure
(approximate-golden-ratio 1/50000)
;;-> Current guess: 1
;;-> Current guess: 2
;;-> Current guess: 3/2
;;-> Current guess: 5/3
;;-> Current guess: 8/5
;;-> Current guess: 13/8
;;-> Current guess: 21/13
;;-> Current guess: 34/21
;;-> Current guess: 55/34
;;-> Current guess: 89/55
;;-> Current guess: 144/89
;;-> Current guess: 233/144
;;-> Current guess: 377/233
;;-> 377/233
```

Which matches the Fibonacci sequence shown below:

``` clojure
(def fibs (lazy-cat [0 1] (map + fibs (rest fibs))))
;;-> #'user/fibs

(take 15 fibs)
;;-> (0 1 1 2 3 5 8 13 21 34 55 89 144 233 377)
```

We can define this sequence of ratios in a way simmilar to how we
define fibs itself (we drop 0 on the low end to avoid dividing by
zero).

``` clojure
(def golden-ratios (map / (drop 2 fibs) (drop 1 fibs)))
;;-> #'user/golden-ratios

(take 15 golden-ratios)
;;-> (1 2 3/2 5/3 8/5 13/8 21/13 34/21 55/34 89/55 144/89 233/144
;;-> 377/233 610/377 987/610

;; Or
(take 15 (iterate #(+ 1 (/ 1 %)) 1)))
;;-> (1 2 3/2 5/3 8/5 13/8 21/13 34/21 55/34 89/55 144/89 233/144
;;-> 377/233 610/377 987/610

(map double (take 15 golden-ratios))
;;-> (1.0 2.0 1.5 1.666666666666667 1.6 1.625 1.615384615384615
;;-> 1.619047619047619 1.617647058823529 1.618181818181818
;;-> 1.617977528089888 1.618055555555556 1.618025751072961
;;-> 1.618037135278515 1.618032786885246)

(map double (take 15 (iterate #(+ 1 (/ 1 %)) 1)))
;;-> (1.0 2.0 1.5 1.666666666666667 1.6 1.625 1.615384615384615
;;-> 1.619047619047619 1.617647058823529 1.618181818181818
;;-> 1.617977528089888 1.618055555555556 1.618025751072961
;;-> 1.618037135278515 1.618032786885246)
  
```

Now we can see that these statements are all true:

``` text
Φ = 1 + (1 / Φ)
Φ = F(n) / F(n - 1)
Φ = 1 + (1 / (F(n) / F(n - 1)))
F(n) = Φ * F(n - 1)
```

This last one is interesting - we can use the golden ratio to
**approximate** the Fibonacci sequence:

``` clojure
(map #(* 1.61803 %) (take 15 fibs))
;;-> (0.0 1.61803 1.61803 3.23606 4.85409 8.090150000000001 12.94424
;;-> 21.034390000000002 33.97863 55.013020000000004 88.99165 144.00467
;;-> 232.99632000000003 377.00099 609.9973100000001)

(map #(int (* 1.61803 %)) (take 15 fibs))
;;-> (0 1 1 3 4 8 12 21 33 55 88 144 232 377 609)

(drop 1 (take 16 fibs))
;;-> (1 1 2 3 5 8 13 21 34 55 89 144 233 377 610)

```

This is only an approximation and does not hold in all cases, but is
interesting to observe nonetheless.
