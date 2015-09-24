; The MIT License (MIT)
;
; Copyright (c) 2014 moo.io - Erhan Bagdemir
;
; Permission is hereby granted, free of charge, to any person obtaining a copy
; of this software and associated documentation files (the "Software"), to deal
; in the Software without restriction, including without limitation the rights
; to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
; copies of the Software, and to permit persons to whom the Software is
; furnished to do so, subject to the following conditions:
;
; The above copyright notice and this permission notice shall be included in
; all copies or substantial portions of the Software.
;
; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
; IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
; FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
; AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
; LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
; OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
; THE SOFTWARE.

(ns io.moo.tars.rendering)

(defmulti prints
  (fn [f, & arg] (class arg)))

(defmethod prints :default [f, & arg]
  (doseq [item arg] (f item))
  (flush))

(defmethod prints String [f, & arg]
  (f arg)
  (flush))

(defmethod prints Character [f, & arg]
  (f arg)
  (flush))

(defmulti render
  (fn [input, metadata] (:type metadata)))

(defmethod render "plain" [input, metadata]
  (vector (format (:format metadata) (first input))))

;; Tabular representation rendering.
(defmethod render "table" [input, metadata]
  (let [cols (:cols metadata)
        hlabels (:headers (:labels metadata))
        hformat (:headers (:format metadata))
        cformat (:columns (:format metadata))]

    (loop [cell input, row [], result []]
      (if (not (empty? cell))
        (if (= (count row) cols)
          (recur (rest cell) (conj [] (first cell)) (conj result (clojure.string/join (map #(format %1 %2) cformat row))))
          (recur (rest cell) (conj row (first cell)) result))
        (if (not (empty? row)) (conj result (clojure.string/join (map #(format %1 %2) cformat row))) result))
      )))
