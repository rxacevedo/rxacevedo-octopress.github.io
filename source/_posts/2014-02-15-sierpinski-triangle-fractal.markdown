---
layout: post
title: "Sierpinski Triangle Fractal"
date: 2014-02-15 22:36:06 -0500
comments: true
categories: clojure, fractal, 
---

Fractals are frequently used to demonstrate recursion in many FP
texts or tutorials. This post will go through the process of rendering
the
[Sierpinksi triangle fractal](http://en.wikipedia.org/wiki/Sierpinski_triangle),
one of the most well-known fractals, and also one of the most straightforward to generate.

We start by determining the coordinates of our outer triangle, as all
other coordinates will be a function of these. This was a bit of a pain
in the ass for me because the JFrame does not use a
[cartesian coordinate system](http://en.wikipedia.org/wiki/Cartesian_coordinate_system),
but rather increases both x and y coordinates by advancing down/to the
right. This means that there are no negative points on the grid. Using
a 500x500 px plane, our triangle's points would be at (250 75), (25
450), and (475 450) for the top, bottom left and bottom right,
respectively.

``` clojure
(def frame
  "java.awt.Frame instance"
  (doto (Frame.)
    (.setVisible true)
    (.setSize 500 500)
    (.setBackground Color/BLACK)))
;;-> nil 

(def ^:dynamic gfx
  "java.awt.Graphics2D instance"
  (doto (.getGraphics frame)
    (.setColor Color/WHITE)))
;;-> nil

(do
  (.drawLine gfx 250 75 25 450)
  (.drawLine gfx 250 75 475 450)
  (.drawLine gfx 25 450 475 450))
```

You should have a white triangle outline on a black background.

{% img left /images/posts/sierpinski_triangle_fractal/sierpinski_0.png %}

The next thing that we need to do is figure out what to do
each time we `recur`, since our procedure will keep doing that
until it terminates. If we look at the progression of the drawing from
the outside in, we see that there is a
[self-similarity](http://en.wikipedia.org/wiki/Self-similarity)
between the outer triangle and those inside of it. For every triangle,
there are three more triangles inside of it. This is the key to our
recursive procedure: **for every one unit of work, we do three units of
work.** What constitutes a unit of work in this case? Well, the most
easily observable thing would be drawing a triangle. If we look
at the Sierpinski triangle, we can see that there are two types of triangles,
the upright (outer) ones, and the inverted (inner) ones. Let's quickly
establish how these two types of triangles should be represented in
our program:

``` clojure
(defn triangle [p1 p2 p3 t-type]
  (condp = t-type
    :outer {:top p1 :b-left p2 :b-right p3}
    :inner {:t-left p1 :t-right p2 :bottom p3}))
```

Each triangle is made up of three points. Depending on the `t-type` we
pass in, our points will be labeled a certain way. This allows us to
specify the type of triangle we are passing in/expecting. This is more
of a convenience method for us, so that we can keep track of the types of
triangles we are handling/drawing in the program. Looking back at our
initial triangle, we can think about what we need to draw in one of two
ways:

1. We need to draw three new triangles, the outer triangles.
2. We need to draw one inverted triangle, the inner triangle.

We will need to do this three times for every one, so it would be wise
to choose the least expensive of the two. This makes our decision
pretty straightforward: for every (outer) triangle, we need to paint its
corresponding inner triangle. Our work still increases as a power of three
each time, but now we are only painting one inner triangle per unit of
work, instead of three outer triangles per unit of work. Since we know
that each recursion requires us to do three more units of work, this
becomes a matter of drawing three inverted triangles per recursive call,
instead of nine outer triangles.

In order to draw the inner triangle for a triangle, we need to
determine its coordinates. We can write a convenience method to do
that for us:

``` clojure
(defn inner [t]
  (let [{:keys [top b-left b-right]} t
        [x1 y1] top
        [x2 y2] b-left
        [x3 y3] b-right]
    (triangle (map double [(+ x2 (/ (- x1 x2) 2)) 
                           (+ y1 (/ (- y2 y1) 2))])
              (map double [(+ x1 (/ (- x3 x1) 2))
                           (+ y1 (/ (- y3 y1) 2))])
              (map double [(+ x2 (/ (- x3 x2) 2))
                           (/ (+ y2 y3) 2)]) :inner)))
```

This function takes a triangle t, and returns a new triangle map for
the inner triangle with its points labeled. The points are calculated
relative to the points of the outer triangle, which reduces our effort
to that of calculating midpoints. Now we can draw the inner triangle
inside of of the outer triangle that we drew before:

``` clojure
(draw-triangle (inner (triangle [250 75] [25 450] [475 450] :outer)))
```

The last crucial bit is determining the coordinates of the outer
triangles, since we will need to draw the corresponding inner triangle
for each. Each of these three new triangles is composed of points from
the outer triangle and the inner triangle. This makes it easy for us
to write another convenience method, this time to return three
triangle maps, one for each triangle:

``` clojure
(defn outers [t]
  (let [{[x1 y1] :top
         [x2 y2] :b-left
         [x3 y3] :b-right} t
        {[ix1 iy1] :t-left
         [ix2 iy2] :t-right
         [ix3 iy3] :bottom} (inner t)]
    {:top (triangle [x1 y1] [ix1 iy1] [ix2 iy2] :outer)
     :b-left (triangle [ix1 iy1] [x2 y2] [ix3 iy3] :outer)
     :b-right (triangle [ix2 iy2] [ix3 iy3] [x3 y3] :outer)}))
```

This function takes a triangle t, and returns a map of triangle maps,
each keyed according to which triangle it represents. It takes
advantage of the procedure `inner` that we just wrote in order to
determine the coordinates of each triangle. Now for each of
these, we can draw its inner:

``` clojure
(doseq [sub-t (vals (outers (triangle [250 75] [25 450] [475 450] :outer)))]
  (draw-triangle (inner sub-t)))
```

Now that we have a method in place for doing this, we just need to
recursively tie it together. We can write one last function,
`sierpinski`, that will handle the recursion for us:

``` clojure
(defn sierpinski
  ([]
     (sierpinski 10))
  ([lvl] (sierpinski (triangle [250 75] [25 450] [475 450] :outer) lvl true))
  ([t lvl]
     (if (> lvl 0)
       (do
         (draw-triangle (inner t))
         (doseq [sub-t (vals (outers t))]
           (sierpinski sub-t (dec lvl))))))
  ([t lvl first?]
     (if first?
       (do 
         (draw-triangle t)
         (sierpinski t lvl false))
       (sierpinski t lvl))))
```

This function, when invoked with no arguments, defaults to a 10-level
Sierpinski triangle. It assumes our JFrame is of size 500x500 and sets
the initial coordinates accordingly. Since we only want to draw the
first outer triangle, we use a boolean argument to control whether the
triangle is drawn, or just its inner triangle. This is only true for
the first iteration. Each time we `recur`, we paint our current
triangle's inner triangle, as well as calling the `sierpinski` method
again on each of the three outer triangles, while decrementing our
level, to do the same thing. These tree-recursive calls to
`sierpinski` mirror the recursive nature of the image itself. Here is
the full source code for the final program:

{% include_code clojure/sierpinski.clj %}

You can suck that into your REPL and run it:

``` clojure
(load-file "/path/to/sierpinski.clj")
;;-> nil

(ns sierpinski)
;;-> nil

(-main)
;;-> nil
```

Or just run this from the same directory as the script:

``` bash
java -cp .:/path/to/clojure.jar clojure.main -m sierpinski sierpinski.clj
```

You should see this output:

{% img /images/posts/sierpinski_triangle_fractal/sierpinski_10.png %}

P.S. You can call `save` with a level and a supplied path string to
save an image for the corresponding Sierpinski triangle (it needs to
repaint it to a new graphics context).
