(ns iGAS.sample
  (:use [incanter core io stats charts excel])
  (:require [clojure.contrib.str-utils2 :as s])
  (:import [org.joda.time.format DateTimeFormat])
  (:import [org.joda.time Interval])
  (:import [java.util Date]))

(def sample-data (read-xls "data/f_sample.xls"))

(defn as-millis
  [date-as-str]
  (.getMillis (.parseDateTime (DateTimeFormat/forPattern "yy-MM-dd HH:mm:ss") date-as-str)))

(defn interval-sec [pre-time nxt-time]
  (.getSeconds (.toPeriod (Interval. (.getTime nxt-time) (.getTime pre-time)))))

(defn make-time-graph [datas] 
  "(view (time-series-plot (map as-millis (map filter-trim (sel sample-data :cols 0))) (sel sample-data :cols 1)))"
  (let [x (map as-millis (map s/trim (sel datas :cols 0)))
	y (sel datas :cols 1)]
    (view (time-series-plot x y))))

;--> sample file to graph
(make-time-graph sample-data)

(defn make-x-from [datas]
  (map as-millis (map s/trim (sel datas :cols 0))))

(defn make-y-from [datas]
  (sel datas :cols 1))

(defn deriv-gas [y]
  (for [xs (partition 2 1 (conj y 0))] (- (nth xs 1) (nth xs 0))))

(defn make-index-data [datas]
  (map vector (make-x-from datas) (for [xs (partition 2 1 (conj (deriv-gas (make-y-from datas)) 0))] (* (nth xs 0) (nth xs 1)))))

(comment (make-index-data sample-data))

(defn find-peek-valley [datas]
  (filter #(> 0 (nth % 1)) (make-index-data datas)))

;--> find peek or valley
(find-peek-valley sample-data)
(defn index-peek-only [datas]
  (map vector (make-x-from datas) (for [xs (partition 2 1 (conj (deriv-gas (make-y-from datas)) 0))] (and (< 0 (nth xs 0)) (> 0 (nth xs 1))))))

(defn find-peek-only [datas]
  (filter #(true? (nth % 1)) (index-peek-only datas)))

;--> find peek only
(find-peek-only sample-data)

(defn indexed-valley-only [datas]
  (map vector (make-x-from datas) (for [xs (partition 2 1 (conj (deriv-gas (make-y-from datas)) 0))] (and (> 0 (nth xs 0)) (< 0 (nth xs 1))))))

(defn indexed-valley-value-only [datas]
  (map vector (make-x-from datas) (for [xs (partition 2 1 (conj (deriv-gas (make-y-from datas)) 0))] (if (and (> 0 (nth xs 0)) (< 0 (nth xs 1))) (nth xs 0) 0))))


(defn find-valley-only [datas]
  (filter #(true? (nth % 1)) (indexed-valley-only datas)))

(defn find-valley-value-only [datas]
  (filter #(> 0 (nth % 1)) (indexed-valley-value-only sample-data)))

;--> find value only
(find-valley-only sample-data)


;;test for validation
 (map (fn[x] (Date. (first x))) (take 10 (find-valley-value-only sample-data)) )
-

;;--> find interval between peeks in only 10
(map #(interval-sec (second %1) (first %1)) (partition 2 1 (map (fn[x] (Date. (first x))) (take 10 (find-peek-only sample-data)) )))

(defn below-than [datas x]
  (map #(if (> x %) true false) (make-y-from datas)))



(defn make-table-peek-value [datas]
  (col-names (conj-cols (map #(Date. %) (make-x-from datas)) (make-y-from datas) (map #(second %) (indexed-valley-only datas)) (map #(second %) (index-peek-only datas)) (below-than datas 11)) [:time :value :valley :peek :cycle]))


(defn ex-time [datas]
  (map (fn[x] (println (Date. (first x)))) (find-valley-only datas)))

(view (make-table-peek-value sample-data))
(save (make-table-peek-value sample-data) "first_Preprocessing_data.csv")
;======================= from this line for test ====================
(comment
;;(def fined-flows (sel flows :filter #(not (string? (nth % 1)))))
;;(def n-data (col-names (conj-cols col0 (sel f_24 :cols 1)) [:time :flow]))
;;(save n-data "test.txt")

  


(defn as-days
  [date-as-str]
  (.getDayOfMonth (.parseDateTime (DateTimeFormat/forPattern "yy-MM-dd HH:mm:ss") date-as-str)))

(defn as-minutes
  [date-as-str]
  (.getSecondOfMinute (.parseDateTime (DateTimeFormat/forPattern "yy-MM-dd HH:mm:ss") date-as-str)))


(defn as-years
  [date-as-str]
  (.getYear (.parseDateTime (DateTimeFormat/forPattern "yy-MM-dd HH:mm:ss") date-as-str)))

(def mod-data (col-names (conj-cols (map as-millis x) (sel flows :cols 1)) [:Date :flow]))

;;(view (time-series-plot (map as-millis x) (sel fined-flows :cols 1)))

(with-data (read-xls "file:///c:/usr/incanter/data/f_sample.xls")
	(let [to-millis (fn [dates] (map #(.getTime %) dates))]
	     (view (time-series-plot (to-millis ($ :time)) ($ :flow)))))

(with-data (read-xls "f24_transed.xls")
	(let [to-millis (fn [dates] (map #(.getTime %) dates))]
	     (view (time-series-plot (to-millis ($ (range 10000) :time)) ($ (range 10000) :flow)))))

)