(ns io.moo.tars.rendering_test
  (:use clojure.test)
  (:require [io.moo.tars.rendering :as renderer]))

(def number-of-columns 3)

(deftest output-plain
  (is
   (= "content"
      (renderer/render
       ["content"] {:type "plain" :format "%s"}))))

(deftest output-tabular
  (is
   (= ["  content1content2content3" "  content4content5content6" "  content7content8"]
      (renderer/render
       ["content1", "content2", "content3", "content4", "content5" , "content6", "content7", "content8"],
       {:cols number-of-columns,
        :labels {:headers ["column1" "column2" "column3"]},
        :type "table",
        :format {:headers ["%10s", "%s", "%s"],
                 :columns ["%10s", "%s", "%s"]
                 }}))))
