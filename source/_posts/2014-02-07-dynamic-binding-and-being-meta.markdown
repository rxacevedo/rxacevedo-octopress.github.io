---
layout: post
title: "Dynamic Binding and Being Meta"
date: 2014-02-07 17:47:38 -0500
comments: true
categories: clojure
---

Yesterday at work I had the perverse urge to send an email to someone
that contained the code used to generate the email itself. The concept
is relatively similar to the below (initially incorrect) example:

``` clojure
(let [a '(println (str "Printing myself: " a))]
  (eval a))
;;-> Printing myself: Unbound: #'user/a
;;-> nil
```
Clearly this doesn't work, but I would like to explore why exactly that is.
First though, we should discuss how Clojure (and Lisps in general)
treat and evaluate functions. When we call a function, we do it in
this way:

``` clojure
(inc 1)
;;-> 2
```

This is just a list of symbols; the Clojure reader will recognize
this list as a call because:

1. It is not empty.
2. It is not quoted.

The first item in the list is assumed to be a special form, a macro,
or a function. This is why this works:

``` clojure
(+ 1 2 3 4 5)
;;-> 15
```

But this does not:

``` clojure
(1 2 3 4 5)
;;-> ClassCastException java.lang.Long cannot be cast to
;;-> clojure.lang.IFn  user/eval12732 (form-init6247023468986614197.clj:1)
```

The exception message sums it up nicely, 1 is not a function and therefore
cannot be called, so we get an error. Many times though, we will want
to create a list and not have it evaluated. We do this by quoting the
list, either with the `quote` function or using a quote literal `'`.

``` clojure
'(1 2 3 4 5)
;;-> (1 2 3 4 5)

'(a list of words and stuff)
;;-> (a list of words and stuff)

(quote (0 1 1 2 3))
;;-> (0 1 1 2 3)

(quote (here have more words))
;;-> (here have more words)
```

There's no difference between a quoted list and an unquoted
list aside from the fact that one is evaluated and one is not.
Likewise, there's no difference between the symbols in a list aside
from how they are treated, which depends on whether or not the list is
evaluted.

``` clojure
'(println (str "This is also" " a list of symbols!"))
;;-> (println (str "This is also" " a list of symbols!"))
;;-> nil

(println (str "This is also" " a list of symbols!"))
;;-> This is also a list of symbols!
;;-> nil
```

If we want to evaluate a list, we can use the `eval` function:

``` clojure
(eval '(take 10 (iterate inc 0)))
;;-> (0 1 2 3 4 5 6 7 8 9)
```

Going back to the example above:

``` clojure
(let [a '(println (str "Printing myself: " a))]
  (eval a))
;;-> Printing myself: Unbound: #'user/a
;;-> nil
```

This doesn't work because the `let` binding is lexically scoped,
meaning that anything declared in the let is bound/accessible only in
that scope, or the immediate textual region. The `eval` function,
however, evaluates forms in a
[null lexical environment](http://www.ai.mit.edu/projects/iiip/doc/CommonLISP/HyperSpec/Body/glo_n.html#null_lexical_environment),
so it cannot see our binding on `a`. That is why when we evaluate the
s-expression using `eval`, we cannot access the binding on the Var - it
is outside of our scope. By that same token, that is why we can have
nested `let`s:

``` clojure
(let [a "YOLO"]
  (let [b "Don't kill my vibe"]
    (let [c "I'm my own woman"]
      (let [d "Go fit or go home"]
        (println (str "Instagram in a nutshell: \n" 
                      (clojure.string/join "\n" [a b c d])))))))
;;-> Instagram in a nutshell:
;;-> YOLO
;;-> Don't kill my vibe
;;-> I'm my own woman
;;-> Go fit or go home
;;-> nil                      
```
Each subsequent `let` is enclosed within the outer `let`, in which the
call to `println` is evaluated. This is exactly what a closure is:

``` clojure
(def a "Hey I'm a Var and I'm accessible throughout this entire namespace!")
;;-> #'user/a
(let [a "But not when we're in a lexical closure, ha!"]
  a)
;;-> "But not when we're in a lexical closure, ha!"
```

To get around this scope issue, we need to ensure that the Var we are trying
to access can be resolved outside of the lexical scope of the call to
`eval`. We can do this by either:

1. Using `def` to declare the Var and bind a value to it.
2. Using `declare` to declare the Var without a binding, and then use
  `binding` to bind a value to it.

Whichever way we do it, we need to ensure that the Var is dynamically
accessible, or all of our work will be for naught. This is done by
declaring or defining the Var with dynamic **metadata**:

``` clojure
(do
  (declare ^:dynamic a)
  (binding [a '(println (str "Printing myself: " a))]
    (eval a)))
 ;;-> Printing myself: (println (str "Printing myself: " a))
 ;;-> nil
```

That's the ticket! Now we can be super cool/meta programmer kidz (if
you have the amazazazing
[pomegranate by Chas Emerick](https://github.com/cemerick/pomegranate) on your classpath):

``` clojure
(require '[cemerick.pomegranate :refer :all])
;;-> nil

(add-dependencies :coordinates '[[com.draines/postal "1.11.1"]]
                  :repositories {"clojars" "http://clojars.org/repo"})
;;-> {[javax.mail/mail "1.4.4" :exclusions [[javax.activation/activation]]] nil,
;;->  [commons-codec "1.7"] nil,
;;->  [com.draines/postal "1.11.1"]
;;->  #{[javax.mail/mail "1.4.4" :exclusions [[javax.activation/activation]]] [commons-codec "1.7"]}}
                  
(require '[postal [core :as postal]])
;;-> nil

(do
  (declare ^:dynamic codez)
    (binding [codez '(postal/send-message ^{:host "smtp.neatwebsite.com"
                                            :user "brogrammer@neatwebsite.com"
                                            :pass "sekr3t!"
                                            :ssl :damn-right!}
                                          {:from "A really cool dude"
                                           :to "you@yoursite.com"
                                           :subject "Incoming: super meta email"
                                           :body (str "Czech it out bby, this code generated this email: \n\n" 
                                                       (binding [*out* (java.io.StringWriter.)]
                                                         (pprint codez)
                                                         (.toString *out*)))})]
      (eval codez)))
;;-> {:error :SUCCESS, :code 0, :message "messages sent"}
```

Awesome! That generates an email that looks like this:

``` text
Czech it out bby, this code generated this email:

(postal/send-message
{:from "A really cool dude",
 :to "you@yoursite.com",
 :subject "Incoming: super meta email",
 :body
 (str
 "Czech it out bby, this code generated this email: \n\n"
 (binding
 [*out* (java.io.StringWriter.)]
 (pprint codez)
 (.toString *out*)))})
```
Don't you feel a billion times cooler now?
