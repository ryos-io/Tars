(ns io.moo.container.rendering)

(defmulti render (fn [x] (:format x)))

;; Plain output rendering.
(defmethod render "plain" [input]
  (:output input))

;; Tabular representation rendering.
(defmethod render "table" [input]
  (let [cols (:cols input)
        rows (:rows input)
        headers (:headers input)
        output-matrix (:output input)
        ]))
