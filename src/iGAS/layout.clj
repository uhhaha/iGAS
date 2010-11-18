(ns iGAS.layout
  (:import [java.awt AlphaComposite Color Graphics Graphics2D]
	   [javax.swing JFrame JPanel]))


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

(defn make-base-layout []
  (proxy [JPanel] []
    (paint [g]
	   (proxy-super paint g)
	   (.setColor g Color/RED)
	   (doseq [xys base-layout]
	     (.drawRect g (nth (:xys xys) 0) (nth (:xys xys) 1) (nth (:xys xys) 2) (nth (:xys xys) 3)))
	   (doseq [xys heat-layout]
	     (.setColor g Color/RED)
	     (.setComposite g (AlphaComposite/getInstance AlphaComposite/SRC_OVER (:alpha xys)))
	     (.fillRect g (nth (:xys xys) 0) (nth (:xys xys) 1) (nth (:xys xys) 2) (nth (:xys xys) 3))))))


(defn draw-mart [title]
  (let [frame (JFrame. title)
	panel (make-base-layout)]
    (doto frame
      (.add panel)
      (.setSize 650 650)
      (.setLocationRelativeTo nil)
      (.setVisible true))))



(draw-mart "MART")