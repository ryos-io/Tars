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

(ns io.moo.container.console
  (:gen-class)
  (:use io.moo.container.commands)
  (:use [clojure.string :only [split, blank?]])
  (:use io.moo.container.os.stty)
  (:import [io.moo.container.commands CommandTemplate]))

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
  (if (not (blank? input))
    (split input #"\s" 2)
    ""))

(defn print-prompt 
  "Prints the command prompt."
  []
  (print "moo> ")
  (flush))

; removes last character from the string.
(defmacro remove-last [ txt ]
  `(subs ~txt 0 (- (count ~txt) 1)))

;; REPL implementation.
(defn repl
  "Read-Eval-Print-Loop implementation"
  []
  (print-prompt)
  (loop [command-buffer nil vertical-cursor-pos 0]

    (let [input-char (.read System/in)]     
      (cond  
       (= input-char 27)
       (do 
         ;; by-pass the first char after escape-char.
         (.read System/in)
         (let [escape-char (.read System/in) ]
           (cond
            (= escape-char 68)
            (if (> vertical-cursor-pos 0)
              (do
                (print (char 27))
                (print (char 91))
                (print (char 68))
                (flush)
                (recur command-buffer (dec vertical-cursor-pos)))
              (recur command-buffer vertical-cursor-pos))
            
            (= escape-char 67)
            (if (and (< vertical-cursor-pos (count command-buffer)))
              (do 
                (print (char 27))
                (print (char 91))
                (print (char 67))
                (flush)
                (recur command-buffer (inc vertical-cursor-pos))   )
              (recur command-buffer vertical-cursor-pos)
              ))
           ))

       ; on enter pressed.
       (= input-char 10)
       (let [input-token (split-parameters command-buffer) ]
         (if 
             (or 
              (blank? command-buffer)
              (not= 
               (perform  
                (CommandTemplate.) 
                (first input-token) 
                (get input-token 1)) :TERMINATE))
           (do 
             (print (char input-char))
             (print-prompt)
             (recur nil 0))))

       ; on backspace entered.
       (= input-char 127)
       (if (not-empty command-buffer)
         (do
           (print "\b \b")
           (flush)
           (recur (remove-last command-buffer) (dec vertical-cursor-pos)))
         (recur command-buffer 0))

       ;; default case
       :else
       (do
         (print (char input-char))
         (flush)
         (recur (str command-buffer (char input-char)) (inc vertical-cursor-pos)))))))
