(ns iGAS.data-processing
  (:use [incanter core io stats charts excel])
  (:use [clojure set])
  (:import [org.joda.time Interval])
  (:import [org.joda.time.format DateTimeFormat])
  (:import [java.util Date]))

(def grocery-data (read-dataset "data/super-data-customer.txt" :header true))

(comment 
(defn get-millis [date-as-str]
  (.getMillis (.parseDateTime (DateTimeFormat/forPattern "yyyy-MM-dd HH:mm:ss") date-as-str)))
)

;(def scantime (map #(get-millis %) (sel grocery-data :cols 2)))

(def trans-scan  (apply assoc {} (flatten (map #(vector % (get-millis %)) (sel grocery-data :cols 2)))))

(view (conj-cols (sel grocery-data :cols 2) (map #(get-millis %) (sel grocery-data :cols 2))))

(def new-db (conj-cols grocery-data (map #(get-millis %) (sel grocery-data :cols 2))))

(comment 
(defn sep-by-miniutes [db stime etime]
  (let [stime (get-millis stime)
	etime (get-millis etime)]
    (sel db :filter #(let [v (nth % 7)] (and (> v stime) (< v etime))))))
)

;(sep-by-miniutes new-db "2005-07-01 12:00:00" "2005-07-01 12:40:00")

(comment
(defn congestion-part [db stime etime]
  (frequencies (try (sel (sep-by-miniutes db stime etime) :cols 3)
		    (catch Exception e nil))))
)

;(defn congestion-part- [db stime etime]
;  (frequencies (sel (sep-by-miniutes db stime etime) :cols 3)))

;(congestion-part new-db "2005-07-01 12:00:00" "2005-07-01 12:40:00")

(defn congestion-analysis [db stime etime] 
  (let [get-millis (fn [date-as-str]
		     (.getMillis (.parseDateTime (DateTimeFormat/forPattern "yyyy-MM-dd HH:mm:ss") date-as-str)))
	sep-by-miniutes (fn [db stime etime]
			  (let [stime (get-millis stime)
				etime (get-millis etime)]
			    (sel db :filter #(let [v (nth % 7)] (and (> v stime) (< v etime))))))
	congestion-part (fn [db stime etime]
			  (frequencies (try (sel (sep-by-miniutes db stime etime) :cols 3)
					    (catch Exception e nil))))
	freq-map (congestion-part db stime etime) 
	diff (difference (set (range 40)) (set (keys freq-map)))
	new-freq (apply assoc freq-map (flatten (map #(vector % 0) diff)))]
    (view (bar-chart (keys (sort new-freq)) (vals (sort new-freq)) :title (str "congestion anlysis" "  "  stime " ~ " etime)))
    new-freq))

(congestion-analysis new-db "2005-07-02 12:01:00" "2005-07-02 12:05:00")

(def fcard (set (sel grocery-data :cols 0)))

(comment 
(defn uniq-trans [card-id]
  (let [uniq-tr (sel grocery-data :filter #(= card-id (nth % 0)))
	fre (set (sel uniq-tr :cols 1))]
    fre))
					;    (assoc temp-map card-id fre)))
)

(defn print-uniq-trans [fcard-set] 
       (println (first fcard-set))
       (try  (println (uniq-trans (first fcard-set)))
	     (catch Exception e nil))
       (if (next fcard-set) (recur (next fcard-set))
	   (println "END")))


(comment 
(defn make-uniq-trans [fcard-set]
       (loop [complete-map {}
	      fcard fcard-set]
	 (if-not (next fcard) complete-map
		 (recur (assoc complete-map
			  (first fcard)  (try 
					  (uniq-trans (first fcard))
					  (catch Exception e nil))) (next fcard)))))
)

;(def visit-freq (make-uniq-trans fcard))


;(defn visit-freq-count [kvs]
;  (map #(vector (key %1) (count (val %1))) kvs))

;(def visit-count-per-card (visit-freq-count visit-freq))
			     
;; graph 
(defn customer-visit-count [fcard]
  (let [uniq-trans (fn [card-id]
		     (let [uniq-tr (sel grocery-data :filter #(= card-id (nth % 0)))
			   fre (set (sel uniq-tr :cols 1))]
		       fre))
	make-uniq-trans  (fn [fcard-set]
			   (loop [complete-map {}
				  fcard fcard-set]
			     (if-not (next fcard) complete-map
				     (recur (assoc complete-map
					      (first fcard)  (try 
							      (uniq-trans (first fcard))
							      (catch Exception e nil))) (next fcard)))))
	visit-freq (make-uniq-trans fcard)
	visit-freq-count (fn [kvs] (map #(vector (key %1) (count (val %1))) kvs))
	visit-count-per-card (visit-freq-count visit-freq)
	x (map #(%1 0) visit-count-per-card )
	y (map #(%1 1) visit-count-per-card)]
;  (view (xy-plot (range (count x)) y))
  (view (scatter-plot (range (count x)) y))))


(defn- group-by-shopping-id [sid]
  (sel grocery-data :filter #(= (nth % 1) sid)))

(defn- group-by-antena [antena]
  (sel grocery-data :filter #(= (nth % 3) antena)))

(defn- stop-per-antena [antena]
  (let [stop (sel (group-by-antena antena) :cols 4)]
    stop))

(let [stops (map #(vector % (mean (stop-per-antena %))) (range 40))
      stop (filter #(> (% 1) 0) stops)
      antena (map #(% 0) stop)
      aver (map #(% 1) stop)]
  (view (bar-chart antena aver :legend true))) 



(let [buyed (sel (sel grocery-data :cols [3 4 5 6]) :filter #(> (nth % 2) 0))
      filter-by-antena (fn [antena] (sel buyed :filter #(= (nth % 0) antena)))
      stops (filter #(> (% 1) 0) (map #(vector % (mean (try (sel (filter-by-antena %) :cols 1) (catch Exception e nil)))) (range 40)))
      antena (map #(% 0) stops)
      aver (map #(% 1) stops)]
  (view stops)
  (view (bar-chart antena aver)))


(def id (sel (sel (sel grocery-data :cols [3 5 6]) :filter #(> (nth % 1) 0)) :cols 0))

(def freq (frequencies id))

(view (sort freq))
(view (bar-chart (keys (sort freq)) (vals (sort freq))))

