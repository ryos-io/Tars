(ns io.moo.tars.rendering)

(defmulti render
  (fn [input, metadata] (:type metadata)))

(defmethod render "plain" [input, metadata]
  (format (:format metadata) (first input) ))

;; Tabular representation rendering.
(defmethod render "table" [input, metadata]
  (let [cols (:cols metadata)
        hlabels (:headers (:labels metadata))
        hformat (:headers (:format metadata))
        cformat (:columns (:format metadata))]

    (loop [cell input, row [], result []]
      (if (not (empty? cell))
        (if (= (count row) cols)
          (recur (rest cell) (conj [] (first cell))  (conj result (clojure.string/join (map #(format %1 %2) cformat row))))
          (recur (rest cell) (conj row (first cell)) result))
        (if (not (empty? row)) (conj result (clojure.string/join (map #(format %1 %2) cformat row))) result))
      )))
