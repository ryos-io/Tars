; The MIT License (MIT)
; 
; Copyright (c) 2014 mooito.org - Erhan Bagdemir
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

(ns org.mooito.moo.console
  (:gen-class)
  (:use org.mooito.moo.commands)
  (:require clojure.string)
  (:use org.mooito.moo.os.stty)
  (:import [org.mooito.moo.commands CommandTemplate]))

;; Macro definition of infinite loop for REPL. 
(defmacro forever [ & body ]
  `(while true ~@body))

;; Prints message of the day on start-up.
(defn print-motd []
  (print "
           (    )
            (oo)
   )\\.-----/(O O)
  # ;       / u
    (  .   |} )
     |/ `.;|/;     Moo version 0.0.1 [ Type 'help' to get help! ]
     \"     \" \"     https://github.com/mooito/moo

")
  (flush))

(defn split-parameters 
  "Split parameters in form of command and parameters"
  [input]
  (if (= input \u2191) (println "up"))
  (if (not (clojure.string/blank? input))
    (clojure.string/split input #"\s" 2)
    ""))

(defn print-prompt 
  "Prints the command prompt."
  []
  (print "moo> ")
  (flush))

;; REPL implementation.
(defn repl
  "Read-Eval-Print-Loop implementation"
  []
  (turn-char-buffering-on)
  (print-motd)
  (print-prompt)
  (loop [command-buffer nil]
    (let [input-char (.read System/in)]     
      (cond  
       (= input-char 10) ;; enter pressed
       (let [ input-token (split-parameters command-buffer) ]
         (print "\n")          
         (if 
             (or 
              (clojure.string/blank? command-buffer)
              (not= (perform  (CommandTemplate.) (first input-token) (get input-token 1)) :TERMINATE))
           (do (print-prompt) (recur nil))))
       (= input-char 127)
         (if (> (count command-buffer) 0)
           (do
             (print "\b \b")
             (flush)
             (recur (subs command-buffer 0 (- (count command-buffer) 1))))
           (recur command-buffer))
       ;; default case
       :else
       (do
         (print (char input-char))
         (flush)
         (recur (str command-buffer (char input-char))))))))
