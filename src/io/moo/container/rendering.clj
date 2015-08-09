(ns io.moo.container.rendering)

(defmulti render (fn [x] (:format x)))
(defmethod render "plain" [input]
  (:output input))
