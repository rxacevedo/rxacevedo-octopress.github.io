---
layout: post
title: "Recursion in Scala"
date: 2013-04-08 19:33
comments: true
categories: programming, scala 
---

I've been going through Martin Odersky's free course on Coursera, [Functional
Programming Principles in Scala](https://www.coursera.org/course/progfun), and
I must say that it's prompted me to think in ways that I've never had to before.
The course starts off teaching by teaching you how basic logic and arithmetic
operators work in Scala, but then quickly dives into recursive functions and
how to use them. In order to help me develop a more concrete
understanding of the concept, and to help anyone else that may be
struggling with it themselves, I've decided to write this blog post to share
what I have learned so far.

Recursive functions are functions that call themselves. This post will focus on
what is called tail-recursion, altough there are also head-recursive functions.
Basically (as far as I understand it), tail-recursion is when a function *does
stuff* and then calls itself as the last step of it's defintion. Head
recursion, on the other hand, is when a function calls itself at the beginning
of its defition, saving that iterations state when the call is made, then
executing any other instructions when the recursive call returns (this would
then happen for each recursive call). A better explanation can be found
[here](http://www.cs.cmu.edu/~adamchik/15-121/lectures/Recursions/recursions.html).

Consider the following function:

``` scala
def sum(a: Int, b: Int): Int = if(a == b) a else a + sum(a + 1, b)
```

Note that the last thing that this function does is call itself. This is a
recursive function in the sense that it is calling itself. As the name
implies, this function will return the sum of parameters a and b. Since the
execution of this wasn't initially evident to me, let's trace it out:

    Call 1: a = 1, b = 5 => sum(1, 5) => (a == b) ? false => 1 + sum(1 + 1, 5) 
    Call 2: a = 2, b = 5 => sum(2, 5) => (a == b) ? false => 2 + sum(2 + 1, 5) 
    Call 3: a = 1, b = 5 => sum(3, 5) => (a == b) ? false => 3 + sum(3 + 1, 5) 
    Call 4: a = 1, b = 5 => sum(4, 5) => (a == b) ? false => 4 + sum(4 + 1, 5) 
    Call 5: a = 1, b = 5 => sum(5, 5) => (a == b) ? true => 5 
    
The evaluation of a and b's equality is the exit condition, or **base
case**. This is necessary with recursive functions, or else execution will go
on forever. Try omitting that and see what happens if you'd like (it'll blow up
in your face). We can see that at the fifth call, our exit condition
evaluates to true, so we return a itself, which is in this case 5. Where do we
return it? To the previous call. Let's further simplify:

``` scala
sum(1,5)
1 + sum(2, 5)
1 + (2 + sum(3, 5))
1 + (2 + (3 + sum(4, 5)))
1 + (2 + (3 + (4 + sum(5, 5))))
1 + (2 + (3 + (4 + (5)))) == 15 // True statement
```

So now we have computed the sum of the numbers between 1 through 5. What if we
wanted to compute the product of these numbers? The key here is that our
function drills down until it reaches the termination condition, then it
"shoots back up" and does an operation on the returned value for each call.
Because we are already returning 5, 4 + (return), 3 + (return), 2 + (return),
and 1 + (return), we already have the basic layout that we want. We want to get
to here: 5, 4 * (return), 3 * (return), 2 * (return), 1 * (return). This is
already starting to look like a factorial function, but we're not quite there
yet. Let's redefine our function to calculate the **product** of the integers
in range (a, b):

``` scala
def product(a: Int, b: Int): Int = if(a == b) a else a * product(a + 1, b)
```

The same thing will happen, only when we reach our termination condition, we
will multiply that by our current *a* value instead of adding it. Let's test that:

```
scala> product(1, 5)
res21: Int = 120
```

So now we have the product, and we only had to change the operator that we
used. Since we are so close, let's go ahead implement a factorial function. We want our
factorial function to take one argument instead of two, we'll call it x:

``` scala
def factorial(x: Int): Int = ???
```
    
Some caveats here, we want to give the function our maxiumum argument, and
calculate recursively going **down**. We'll have to tweak the function a little
bit to do that. We know that:

- We have to decrement our argument for each recursive call
- We'll want to terminate at some point
  
``` scala
def factorial(x: Int): Int = if(???) ??? else x * factorial(x - 1) 
```

We want to multiply our inital value by what is returned by the recursive call,
that call being on the current argument decremented by 1. When should we
terminate though, and what should we return? I have a habit of checking if a
value is 0, and one *could* return 1 when this is met , but that would give us
an extra one, 5 * 4 * 3 * 2 * 1 * 1, which is an innocuous value to multiply
by, but increases the depth of our call stack nonetheless. In this case it's not
necessary, since 5 * 4 * 3 * 2 * 1 will give us our answer. We should check
when we have reached 1 (and that our input is not negative), and return 1 (to multiply by) in that case. Our new
definition looks like this:

``` scala 
def factorial(x: Int): Int = if(x >= 1) 1 else x * factorial(x - 1)
```

Now let's run that puppy: 

```
scala> factorial(5)
res22: Int = 120
```

And we get what we expected. 

Now, this *looks* very elegant, doesn't it? Well, we can further simplify.
If you look back up at the execution of the method, you'll see that we have to
remember the value of every single iteration, all the way up the
chain. Our expression grows with each execution, not so much for the sum of 15,
but you can imagine how quickly it would grow for factorial(200) (well, that returns
infinity using type Double). What we need is a function that doesn't depend on
all previous iterations, one that is given every value that it needs to compute
the value for that iteration, instead of having to run all the way up the
heirarchy, doing a sum (or multiplication) for each call (within the arguments).
We can do this by defining an inner function and supplying it with a seed value.
Lets start with our sum function:

``` scala 
def sum(a: Int, b: Int) = {
  def inner(acc: Int, a: Int, b: Int): Int = if (a > b) acc else inner(acc + a, a + 1, b)
    inner(0, a, b)
} 
```

In this implementation, we define an inner function that takes three arguments:
our two previous arguments, and an accumulator. Accumulator is your friend,
because by passing him into the function each time, we avoid having to keep
track of what's going on in the call hierarchy above us, we have everything we
need because accumulator is storing a running total. Also notice that our
function does not have to do any calculations upon returning, unlike the
previous implementation that needed to add a to the return value. This function
is **tail recursive**. We can take advantage of the accumulator and use the
same approach with our product and factorial functions:

``` scala
def product(a: Int, b: Int) = {
  def inner(acc: Int, a: Int, b: Int): Int = if (a > b) acc else inner(acc * a, a + 1, b)
    inner(1, a, b)
}
```

Note that we seed 1 to this function instead of 0 since we don't want to
multiply by zero.

Factorial is just as easy:

``` scala
def factorial(a: Int) = {
  def inner(acc: Int, a: Int): Int = if (a == 0) acc else inner(acc * a, a - 1)
    inner(1, a)
}
```

By doing this, we can reuse the same stack frame for each call. This is in
contrast to our previous approaches, which required that we push onto the stack
for each consecutive calculation and call, then pop each return off the
stack in order to arrive at our final value. The first approach is a **recursive process**, whereas the second is called an **iterative process.**
