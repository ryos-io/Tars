(ns io.moo.container.rendering_test
  (:use clojure.test)
  (:require [io.moo.container.rendering :as renderer]))


(deftest output-plain
  (is (= "output" (renderer/render {:type "plain" :output "output"}))))

(deftest output-tabular
  (is
   (= "<test-should-fail>"
      (renderer/render
       {:cols 3,
        :labels {
                 :headers ["column1" "column2" "column3"]
                 },
        :type "table",
        :format {
                 :headers [],
                 :columns []
                 }
        }
       ))))
