---
layout: post
title: "Clojure REPLing - Orders of Growth Analysis With Incanter"
date: 2014-01-03 18:06:59 -0500
comments: true
categories: 
published: false
---

I've been messing around with Incanter for the past two days now and have had a 
great experience thus far, prompting me to write a little bit about it. Incanter 
is (as it states on [the homepage](http://incanter.org))  a computing and graphics 
library built on the Clojure platform that strives to be semantically similar to R. 
The true power in using Incanter, though (for me, anyways), is that it can be used 
directly from the Clojure REPL working directly with Clojure data structures. Because 
Clojure is so malleable, we can tranform our data easily and feed it directly into 
Incanter, allowing us to observe statistics about the data as well as to plot it on 
beautiful charts. The snippet below produces a line-chart based on data generated 
using Clojure's time function:

``` clojure Line Chart
(let [coll-sizes (map #(expt 2 %) (range 5 10)) ;; Set up the collection sizes to be used
	  methods ['rvrs 'my-rvrs 'reverse]] ;; Hold method names in a collection as symbols                  
  (-> (for [x methods 
			y coll-sizes
			:let [z (binding [*out* (java.io.StringWriter.)] ;; Pipe stdout to a StringWriter (x * y instantiations)
					  (time ((resolve x) (range 0 y))) ;; Resolve x (symbol) to actual function and apply it to coll (size y), timing the running time and printing it to the StringWriter
					  (->> (.. *out* toString) ;; Retrieve output from StringWriter
						   (re-find #"\d+.\d+") ;; Parse out the numeric value (running time)
						   Double/parseDouble))]] 
		[x y z]) ;; Convert to double for plotting
	  to-dataset ;; Convert the yielded list an Incanter dataset
	  (with-data 
		(save-pdf (line-chart :col-1 :col-2 
							  :group-by :col-0 
							  :x-label "coll-size" 
							  :y-label "runtime (ms)" 
							  :legend true) "/Users/Roberto/Desktop/reversals.pdf")))) ;; Plot the data and save it to a PDF

;; The for-comprehension yields a collection of this structure:

;; ([rvrs 32 0.086]
;;  [rvrs 64 0.145]
;;  [rvrs 128 0.485]
;;  [rvrs 256 1.717]
;;  [rvrs 512 7.809]
;;  [my-rvrs 32 0.018]
;;  [my-rvrs 64 0.016]
;;  [my-rvrs 128 0.024]
;;  [my-rvrs 256 0.042]
;;  [my-rvrs 512 0.082]
;;  [reverse 32 0.009]
;;  [reverse 64 0.007]
;;  [reverse 128 0.014]
;;  [reverse 256 0.028]
;;  [reverse 512 0.051])

;; And calling to-dataset on it yields this output in the REPL:

;; |  :col-0 | :col-1 | :col-2 |
;; |---------+--------+--------|
;; |    rvrs |     32 |  0.073 |
;; |    rvrs |     64 |  0.132 |
;; |    rvrs |    128 |  0.473 |
;; |    rvrs |    256 |  1.704 |
;; |    rvrs |    512 |  6.696 |
;; | my-rvrs |     32 |   0.01 |
;; | my-rvrs |     64 |  0.012 |
;; | my-rvrs |    128 |  0.022 |
;; | my-rvrs |    256 |  0.043 |
;; | my-rvrs |    512 |  0.078 |
;; | reverse |     32 |  0.007 |
;; | reverse |     64 |  0.011 |
;; | reverse |    128 |  0.014 |
;; | reverse |    256 |  0.026 |
;; | reverse |    512 |  0.054 |

```

It generates [this PDF](/downloads/code/incanter/reversals.pdf).
