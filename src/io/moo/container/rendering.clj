(ns io.moo.container.rendering)

(defmulti render (fn [x] (:type x)))

;; Plain output rendering.
(defmethod render "plain" [input]
  (:output input))

;; Tabular representation rendering.
(defmethod render "table" [input]
  (let [cols (:cols input)
        hlabels (:labels (:headers input))
        hformat (:format (:headers input))
        cformat (:format (:columns input))
        ]))
