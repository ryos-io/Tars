(ns io.moo.container.rendering)

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
    
    (loop [cell input, row nil]
      (println cell)
      (if (not (empty? cell))
        (if (> (count row) cols)
          (do (recur (rest cell) (conj row (first input))))
          (recur (rest cell) nil)
          )))))
