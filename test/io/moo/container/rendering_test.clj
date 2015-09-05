(ns io.moo.container.rendering_test
  (:use clojure.test)
  (:require [io.moo.container.rendering :as renderer]))

(def number-of-columns 3)

(deftest output-plain
  (is
   (= "content"
      (renderer/render
       ["content"] {:type "plain" :format "%s"}))))

(deftest output-tabular
  (is
   (= "<test-should-fail>"
      (renderer/render
       ["content1", "content2", "content3", "content4", "content" , "content6", "content7"],
       {:cols number-of-columns,
        :labels {:headers ["column1" "column2" "column3"]},
        :type "table",
        :format {:headers ["format1", "format2", "format3"],
                 :columns ["format1", "format2", "format3"]
                 }}))))
