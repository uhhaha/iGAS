(ns iGAS.contour
  (:import [java.awt AlphaComposite Color Graphics Graphics2D]
	   [javax.swing JFrame JPanel])
  (:use [incanter core stats processing]))


(defstruct shelf :xys :alpha)

(def base-layout (map #(struct shelf %) [ [10 10 50 450]
					  [130 10 300 50]
					  [130 110 300 50]
					  [130 210 300 50]
					  [130 310 300 50]]))

(def heat-layout (map #(struct shelf %1 %2) [[130 60 300 50]
					     [130 160 250 50]
					     [130 260 300 50]
					     [130 360 300 50]]
		      [0.5 0.7 0.9 1.0]))

(defstruct fpoint :x :y :value)

(def f1 (struct fpoint 100 100 3))
(def f2 (struct fpoint 100 300 0))
(def f3 (struct fpoint 300 100 3.5))
(def f4 (struct fpoint 300 300 0))

(defn bilinear [x y]
  (let [A (trans (matrix [(- 1 x) x]))
	B (matrix [[(f1 :value) (f2 :value)]
		   [(f3 :value) (f3 :value)]])
	C (matrix [(- 1 y) y])]
    (mmult A B C)))

(defn make-interolation [xx yy rang ww] 
     (let [xs (for [x (range xx rang ww) y (range yy rang ww)] 
		(struct fpoint x y (bilinear x y)))
	   min (apply min (map #(:value %) xs))
	   max (apply max (map #(:value %) xs))]
       (map #(assoc % :value (norm (% :value) min max)) xs)))
      
(defn make-base-layout []
  (proxy [JPanel] []
    (paint [g]
	   (proxy-super paint g)
	   (.setColor g Color/RED)
	   (doseq [xys (make-interolation 0 0 200 10)]
	     (.setColor g Color/BLUE)
	     (.setComposite g (AlphaComposite/getInstance AlphaComposite/SRC_OVER (:value xys)))
	     (.fillRect g (xys :x) (xys :y) 10 10)
	     )
	   (doseq [xys (make-interolation 200 0 400 10)]
	     (.setColor g Color/BLUE)
	     (.setComposite g (AlphaComposite/getInstance AlphaComposite/SRC_OVER (:value xys)))
	     (.fillRect g (xys :x) (xys :y) 10 10)
	     )
	   )))

(defn draw-contour [title]
  (let [frame (JFrame. title)
	panel (make-base-layout)]
    (doto frame
      (.add panel)
      (.setSize 600 600)
      (.setLocationRelativeTo nil)
      (.setVisible true))))



(draw-contour "MART")