(ns sierpinski
  (import [java.awt Color]
          [java.awt.image BufferedImage]
          [javax.imageio ImageIO]
          [javax.swing JFrame])
  (:gen-class))

(def frame
  "java.awt.JFrame instance"
  (doto (JFrame.)
    (.setVisible true)
    (.setSize 500 500)
    (.setBackground Color/BLACK)))

(def ^:dynamic gfx
  "java.awt.Graphics2D instance"
  (doto (.getGraphics frame)
    (.setColor Color/WHITE)))

(defn triangle [p1 p2 p3 t-type]
  "Takes three points p1, p2, and p3 and returns
   a triangle map of type t-type. t-type is used
   to specify the ordering/labeling of the input
   coordinates:

   :outer {:top p1 :b-left p2 :b-right p3}
   :inner {:t-left p1 :t-right p2 :bottom p3}"
  (condp = t-type
    :outer {:top p1 :b-left p2 :b-right p3}
    :inner {:t-left p1 :t-right p2 :bottom p3}))

(defn inner [t]
  "Takes an :outer triangle t and returns a map of
   coordinates for its inner (inverse) triangle."
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

(defn outers [t]
  "Takes an :outer triangle t and returns a map of triangle
   maps labeled :top :b-left and :b-right that represent
   the triangle's three outer triangles."
  (let [{[x1 y1] :top
         [x2 y2] :b-left
         [x3 y3] :b-right} t
        {[ix1 iy1] :t-left
         [ix2 iy2] :t-right
         [ix3 iy3] :bottom} (inner t)]
    {:top (triangle [x1 y1] [ix1 iy1] [ix2 iy2] :outer)
     :b-left (triangle [ix1 iy1] [x2 y2] [ix3 iy3] :outer)
     :b-right (triangle [ix2 iy2] [ix3 iy3] [x3 y3] :outer)}))

(defn draw-triangle [t]
  "Takes a triangle t and draws it."
  (let [[[x1 y1]
         [x2 y2]
         [x3 y3]] (vals t)]
    (do
      (.drawLine gfx x1 y1 x2 y2)
      (.drawLine gfx x1 y1 x3 y3)
      (.drawLine gfx x2 y2 x3 y3))))

;; Entry method
(defn sierpinski
  ([]
     (sierpinski 10))
  ([lvl] (sierpinski (triangle [250 75] [25 450] [475 450] :outer) lvl true))
  ([t lvl]                         
     (when (> lvl 0)
       (draw-triangle (inner t))
       (doseq [sub-t (vals (outers t))]
         (sierpinski sub-t (dec lvl)))))
  ([t lvl first?]
     (if first?
       (do 
         (draw-triangle t)
         (sierpinski t lvl false))
       (sierpinski t lvl))))

(defn clear [gfx]
  "Clears the Graphics2D context"
  (.clearRect gfx 0 0 500 500))

(defn save [lvl path]
  "Binds gfx to the Graphics context of a new
   BufferedImage, calls sierpinski to draw on it,
   and then writes the BufferedImage contents to a
   png file at path."
  (let [c (.getContentPane frame)
        bi (BufferedImage. (.getWidth c) (.getHeight c) BufferedImage/TYPE_INT_RGB)
        new-gfx (.createGraphics bi)]
    (binding [gfx new-gfx]
      (clear gfx)    
      (sierpinski lvl)
      (ImageIO/write bi "PNG" (java.io.File. path)))))

(defn -main [& args]
  "Main method"
  (clear gfx) 
  (sierpinski))
