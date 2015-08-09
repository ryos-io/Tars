(ns io.moo.container.rendering_test
  (:use clojure.test)
  (:require [io.moo.container.rendering :as renderer]))


(deftest output-plain
  (is (= "output" (renderer/render {:format "plain" :output "output"}))))
